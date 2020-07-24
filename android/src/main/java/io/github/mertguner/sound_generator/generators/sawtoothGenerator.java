package io.github.mertguner.sound_generator.generators;

public class sawtoothGenerator extends baseGenerator {
    public short getValue(double phase, double period) {
        if (phase < (period / 2))
            return (short)(Short.MAX_VALUE * (((2. * phase) / Math.PI) - 1));
        else
            return (short)(Short.MAX_VALUE * (((2. * phase) / Math.PI) - 3));
    }
}
