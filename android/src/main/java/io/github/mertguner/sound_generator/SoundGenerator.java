package io.github.mertguner.sound_generator;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import io.github.mertguner.sound_generator.generators.sawtoothGenerator;
import io.github.mertguner.sound_generator.generators.signalDataGenerator;
import io.github.mertguner.sound_generator.generators.sinusoidalGenerator;
import io.github.mertguner.sound_generator.generators.squareWaveGenerator;
import io.github.mertguner.sound_generator.generators.triangleGenerator;
import io.github.mertguner.sound_generator.handlers.isPlayingStreamHandler;
import io.github.mertguner.sound_generator.models.WaveTypes;

public class SoundGenerator {

    private Thread bufferThread;
    private AudioTrack audioTrack;
    private signalDataGenerator generator;
    private boolean isPlaying = false;
    private int minSamplesSize;
    private WaveTypes waveType = WaveTypes.SINUSOIDAL;
    private float rightVolume = 1, leftVolume = 1, volume = 1, dB = -20;
    private boolean cleanStart = false;

    public void setCleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
    }

    public void setAutoUpdateOneCycleSample(boolean autoUpdateOneCycleSample) {
        if (generator != null)
            generator.setAutoUpdateOneCycleSample(autoUpdateOneCycleSample);
    }

    public int getSampleRate() {
        if (generator != null)
            return generator.getSampleRate();
        return 0;
    }

    public void setSampleRate(int sampleRate) {
        if (generator != null)
            generator.setSampleRate(sampleRate);
    }

    public void refreshOneCycleData() {
        if (generator != null)
            generator.createOneCycleData(true);
    }

    public void setFrequency(float v) {
        if (generator != null)
            generator.setFrequency(v);
    }

    public float getFrequency() {
        if (generator != null)
            return generator.getFrequency();
        return 0;
    }

    public void setBalance(float balance) {
        balance = Math.max(-1, Math.min(1, balance));

        rightVolume = (balance >= 0) ? 1 : (balance == -1) ? 0 : (1 + balance);
        leftVolume = (balance <= 0) ? 1 : (balance == 1) ? 0 : (1 - balance);
        if (audioTrack != null) {
            audioTrack.setStereoVolume(leftVolume, rightVolume);
        }
    }


    public void setVolume(float volume, boolean recalculateDecibel) {
        volume = Math.max(0, Math.min(1, volume));
        this.volume = volume;

        if(recalculateDecibel) {
            if (volume >= 0.000001f) {
                this.dB = 20f * (float) Math.log10(volume);
            } else {
                this.dB = -120f;
            }
        }

        if (audioTrack != null) {
            audioTrack.setStereoVolume(leftVolume * volume, rightVolume * volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setDecibel(float dB) {
        this.dB = dB;
        float lineerVolume = (float) Math.pow(10f, (dB / 20f) );
        if (lineerVolume < 0.000001f) {
            lineerVolume = 0f;
        }
        setVolume(lineerVolume, false);
    }

    public float getDecibel() {
        return dB;
    }

    public void setWaveform(WaveTypes waveType) {
        if (this.waveType.equals(waveType) || (generator == null))
            return;

        this.waveType = waveType;

        if (waveType.equals(WaveTypes.SINUSOIDAL))
            generator.setGenerator(new sinusoidalGenerator());
        else if (waveType.equals(WaveTypes.TRIANGLE))
            generator.setGenerator(new triangleGenerator());
        else if (waveType.equals(WaveTypes.SQUAREWAVE))
            generator.setGenerator(new squareWaveGenerator());
        else if (waveType.equals(WaveTypes.SAWTOOTH))
            generator.setGenerator(new sawtoothGenerator());
    }

    public boolean init(int sampleRate) {
        try {
            minSamplesSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            generator = new signalDataGenerator(minSamplesSize, sampleRate);

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minSamplesSize,
                    AudioTrack.MODE_STREAM);

            return true;
        }catch (Exception ex)
        {
            return false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void startPlayback() {
        if (bufferThread != null || audioTrack == null) return;

        isPlaying = true;

        if (cleanStart) {
            generator.resetFrequency();
            generator.updateOnce();
        }

        bufferThread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioTrack.flush();
                audioTrack.setPlaybackHeadPosition(0);
                audioTrack.play();
                while (isPlaying) {
                    audioTrack.write(generator.getData(), 0, minSamplesSize);
                }
            }
        }
        );

        isPlayingStreamHandler.change(true);

        bufferThread.start();
    }

    public void stopPlayback() {
        if (bufferThread == null) return;

        isPlaying = false;

        try {
            bufferThread.join(); //Waiting thread
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isPlayingStreamHandler.change(false);
        bufferThread = null;

        if (audioTrack != null) {
            audioTrack.stop();
        }
    }

    public void release() {
        if (isPlaying())
            stopPlayback();
        audioTrack.release();
    }

}
