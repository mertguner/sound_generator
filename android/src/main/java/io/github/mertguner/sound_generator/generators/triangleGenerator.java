package io.github.mertguner.sound_generator.generators;

public class triangleGenerator extends baseGenerator {
    public short getValue(double phase, double period) {
        if(phase <= (period / 4))
            return (short) (Short.MAX_VALUE * (((4 * phase) / Math.PI) - 1.));
        else if(phase <= (period / 2))
            return (short) (Short.MAX_VALUE * (3. - ((4 * phase) / Math.PI)));
        else if(phase <= (3*period / 4))
            return (short) (Short.MAX_VALUE * (((4 * phase) / Math.PI) - 5.));
        else
            return (short) (Short.MAX_VALUE * (7. - ((4 * phase) / Math.PI)));
    }
}
