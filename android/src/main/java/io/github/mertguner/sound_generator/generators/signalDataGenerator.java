package io.github.mertguner.sound_generator.generators;

import java.util.ArrayList;
import java.util.List;

import io.github.mertguner.sound_generator.handlers.getOneCycleDataHandler;

public class signalDataGenerator {

    public static final int SAMPLE_RATE = 96000;
    private final float _2Pi = 2.0f * (float) Math.PI;
    private final float phCoefficient = _2Pi / (float) SAMPLE_RATE;
    private final float smoothStep = 1f / (float) SAMPLE_RATE * 20f;

    private float frequency = 50;
    private baseGenerator generator = new sinusoidalGenerator();

    private short[] backgroundBuffer;
    private short[] buffer;
    //private short[] oneCycleBuffer;
    private List<Integer> oneCycleBuffer = new ArrayList<>();
    private int bufferSamplesSize;
    private float ph = 0;
    private float oldFrequency = 50;
    private boolean creatingNewData = false;

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

    public signalDataGenerator(int bufferSamplesSize) {
        this.bufferSamplesSize = bufferSamplesSize;
        backgroundBuffer = new short[bufferSamplesSize];
        buffer = new short[bufferSamplesSize];
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
        if (generator == null)
            return;

        int size = Math.round(_2Pi / (frequency * phCoefficient));

        oneCycleBuffer.clear();
        for (int i = 0; i < size; i++) {
            oneCycleBuffer.add((int)generator.getValue((frequency * phCoefficient) * (float) i, _2Pi));
        }
        oneCycleBuffer.add((int)generator.getValue(0, _2Pi));// For full Cycle view
        getOneCycleDataHandler.setData(oneCycleBuffer);
    }
}
