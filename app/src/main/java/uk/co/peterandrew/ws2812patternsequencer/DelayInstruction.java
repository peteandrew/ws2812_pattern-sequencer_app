package uk.co.peterandrew.ws2812patternsequencer;

import java.util.ArrayList;
import java.util.List;

public class DelayInstruction extends Instruction {
    private byte delay;

    public DelayInstruction(byte delay) {
        this.delay = delay;
    }

    public String instructionName() {
        return "Delay";
    }

    public byte instructionByte() {
        return 0x03;
    }

    public List<Byte> argumentsBytes() {
        List<Byte> args = new ArrayList<>();
        args.add(delay);
        return args;
    }

    public String argumentsToString() {
        return "Delay: " + Byte.toUnsignedInt(delay);
    }

    public int numBytes() {
        return 2;
    }
}
