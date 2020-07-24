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

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class SoundGenerator {

    private Thread mThread;
    private AudioTrack mAudioTrack;
    private signalDataGenerator generator;
    private boolean shouldPlay = false;
    private int minSamplesSize;
    private WaveTypes waveType = WaveTypes.SINUSOIDAL;

    public int getSampleRate(){ return signalDataGenerator.SAMPLE_RATE; }
    public void refreshOneCycleData()
    {
        if(generator != null)
            generator.createOneCycleData();
    }
    public void setFrequency(float v){
        if(generator != null)
            generator.setFrequency(v);
    }

    public void setBalance(float balance) {
        float right = (balance >= 0) ? 1 : (balance == -1) ? 0 : (1 + balance);
        float left = (balance <= 0) ? 1 : (balance == 1) ? 0 : (1 - balance);
        if (mAudioTrack != null) {
            mAudioTrack.setStereoVolume(left, right);
        }
    }

    public void setWaveform(WaveTypes waveType){
        if(this.waveType.equals(waveType) || (generator == null))
            return;

        this.waveType = waveType;

        if(waveType.equals(WaveTypes.SINUSOIDAL))
            generator.setGenerator(new sinusoidalGenerator());
        else if(waveType.equals(WaveTypes.TRIANGLE))
            generator.setGenerator(new triangleGenerator());
        else if(waveType.equals(WaveTypes.SQUAREWAVE))
            generator.setGenerator(new squareWaveGenerator());
        else if(waveType.equals(WaveTypes.SAWTOOTH))
            generator.setGenerator(new sawtoothGenerator());
    }

    public SoundGenerator(){
        minSamplesSize = AudioTrack.getMinBufferSize(
                signalDataGenerator.SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        generator = new signalDataGenerator(minSamplesSize);

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                signalDataGenerator.SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minSamplesSize,
                AudioTrack.MODE_STREAM);
    }

    public boolean isPlaying() {
        return mThread != null;
    }

    public void startPlayback() {
        if (mThread != null) return;

        shouldPlay = true;

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioTrack.flush();
                mAudioTrack.setPlaybackHeadPosition(0);
                mAudioTrack.play();
                while (shouldPlay) {
                    mAudioTrack.write(generator.getData(), 0, minSamplesSize);
                }
            }
        });

        isPlayingStreamHandler.change(true);

        mThread.start();
    }


    public void stopPlayback() {
        if (mThread == null) return;

        shouldPlay = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isPlayingStreamHandler.change(false);
        mThread = null;

        if(mAudioTrack != null) {	            // Turn off looping
            mAudioTrack.stop();
        }
    }

    public void release() {
        if(isPlaying())
            stopPlayback();
        mAudioTrack.release();
    }

}
