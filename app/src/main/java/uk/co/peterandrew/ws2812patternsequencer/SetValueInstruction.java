package uk.co.peterandrew.ws2812patternsequencer;

import java.util.ArrayList;
import java.util.List;

public class SetValueInstruction extends Instruction {
    private byte ledNum;
    private byte red;
    private byte green;
    private byte blue;

    public SetValueInstruction(byte ledNum, byte red, byte green, byte blue) {
        this.ledNum = ledNum;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String instructionName() {
        return "Set value";
    }

    public byte instructionByte() {
        return 0x02;
    }

    public List<Byte> argumentsBytes() {
        List<Byte> args = new ArrayList<>();
        args.add(ledNum);
        args.add(red);
        args.add(green);
        args.add(blue);
        return args;
    }

    public String argumentsToString() {
        String ret = "";
        ret += "Led: " + ledNum;
        ret += ", red: " + Byte.toUnsignedInt(red);
        ret += ", green: " + Byte.toUnsignedInt(green);
        ret += ", blue: " + Byte.toUnsignedInt(blue);
        return ret;
    }

    public int numBytes() {
        return 5;
    }
}