package uk.co.peterandrew.ws2812patternsequencer;

import java.util.List;

public abstract class Instruction {
    public abstract String instructionName();

    public abstract byte instructionByte();

    public abstract List<Byte> argumentsBytes();

    public abstract String argumentsToString();

    public abstract int numBytes();
}