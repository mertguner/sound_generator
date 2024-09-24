package io.github.mertguner.sound_generator.generators;

import java.util.ArrayList;
import java.util.List;

import io.github.mertguner.sound_generator.handlers.getOneCycleDataHandler;

public class signalDataGenerator {


    private final float _2Pi = 2.0f * (float) Math.PI;

    private int sampleRate = 48000;
    private float phCoefficient = _2Pi / (float) sampleRate;
    private float smoothStep = 1f / (float) sampleRate * 20f;

    private float frequency = 50;
    private baseGenerator generator = new sinusoidalGenerator();

    private short[] backgroundBuffer;
    private short[] buffer;
    private List<Integer> oneCycleBuffer = new ArrayList<>();
    private int bufferSamplesSize;
    private float ph = 0;
    private float oldFrequency = 50;
    private boolean creatingNewData = false;
    private boolean autoUpdateOneCycleSample = false;

    public boolean isAutoUpdateOneCycleSample() { return autoUpdateOneCycleSample; }
    public void setAutoUpdateOneCycleSample(boolean autoUpdateOneCycleSample) { this.autoUpdateOneCycleSample = autoUpdateOneCycleSample; }

    public int getSampleRate() { return sampleRate; }
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        phCoefficient = _2Pi / (float) sampleRate;
        smoothStep = 1f / (float) sampleRate * 20f;
    }

    public baseGenerator getGenerator() {
        return generator;
    }
    public void setGenerator(baseGenerator generator) {
        this.generator = generator;
        createOneCycleData();
    }

    public float getFrequency() {
        return frequency;
    }
    public void setFrequency(float frequency) {
        this.frequency = frequency;
        createOneCycleData();
    }

    public void resetFrequency() {
        oldFrequency = frequency;
    }

    public signalDataGenerator(int bufferSamplesSize, int sampleRate) {
        this.bufferSamplesSize = bufferSamplesSize;
        backgroundBuffer = new short[bufferSamplesSize];
        buffer = new short[bufferSamplesSize];
        setSampleRate(sampleRate);
        updateOnce();
    }

    public void updateOnce() {
        updateData();
        createOneCycleData();
    }

    private void updateData() {
        creatingNewData = true;
        for (int i = 0; i < bufferSamplesSize; i++) {
            oldFrequency += ((frequency - oldFrequency) * smoothStep);
            backgroundBuffer[i] = generator.getValue(ph, _2Pi);
            ph += (oldFrequency * phCoefficient);

            //performance of this block is higher than ph %= _2Pi;
            // ifBlock  Test score =  2,470ns
            // ModBlock Test score = 27,025ns
            if (ph > _2Pi) {
                ph -= _2Pi;
            }
        }
        creatingNewData = false;
    }

    public short[] getData() {
        if (!creatingNewData) {
            System.arraycopy(backgroundBuffer, 0, buffer, 0, bufferSamplesSize);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateData();
                }
            }).start();
        }
        return this.buffer;
    }

    public void createOneCycleData() {
        createOneCycleData(false);
    }

    public void createOneCycleData(boolean force) {
        if (generator == null || (!autoUpdateOneCycleSample && !force))
            return;

        int size = Math.round(_2Pi / (frequency * phCoefficient));

        oneCycleBuffer.clear();
        for (int i = 0; i < size; i++) {
            oneCycleBuffer.add((int)generator.getValue((frequency * phCoefficient) * (float) i, _2Pi));
        }
        oneCycleBuffer.add((int)generator.getValue(0, _2Pi));
        getOneCycleDataHandler.setData(oneCycleBuffer);
    }
}
