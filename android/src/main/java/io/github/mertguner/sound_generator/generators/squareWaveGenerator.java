package io.github.mertguner.sound_generator.generators;

public class squareWaveGenerator extends baseGenerator {
    public short getValue(double phase, double period) {
        if(phase <= (period / 2)) {
            return Short.MAX_VALUE;
        } else {
            return -Short.MAX_VALUE;
        }
    }
}
