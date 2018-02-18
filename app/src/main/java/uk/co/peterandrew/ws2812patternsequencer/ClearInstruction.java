package uk.co.peterandrew.ws2812patternsequencer;

import java.util.ArrayList;
import java.util.List;

public class ClearInstruction extends Instruction {
    public String instructionName() {
        return "Clear all";
    }

    public byte instructionByte() {
        return 0x01;
    }

    public List<Byte> argumentsBytes() {
        return new ArrayList<>();
    }

    public String argumentsToString() {
        return "";
    }

    public int numBytes() {
        return 1;
    }
}