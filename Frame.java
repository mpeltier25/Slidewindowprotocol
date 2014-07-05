import java.io.*;
import java.util.*;
import java.math.*;

public class Frame
{
    private byte[]frameBytes = new byte[18];

    public static final int SIZE = 18;
    public static final int PAYLOAD = 12;
    public static final byte FRAMING_BYTE = (byte) 126;
    public static final int START = 0;
    public static final int MESSAGE_ID = 1;
    public static final int SEQUENCE_NUMBER = 2;
    public static final int DATA = 3;
    public static final int CRC = 15;
    public static final int END = 17;
    public static final int ACK = 1;
    public static final int NACK = 0;
    public Frame(byte[]data){
        data = Arrays.copyOf(data,Frame.PAYLOAD);
        byte[]crc16 = CRC16.go(data);
        frameBytes[Frame.START] = frameBytes[Frame.END] = Frame.FRAMING_BYTE;
        frameBytes[Frame.MESSAGE_ID] =
            (byte)(new Random(new BigInteger(crc16).longValue())).nextInt(256);
        for(int i = 0; i < data.length; i++)
            frameBytes[i+Frame.DATA] = data[i];
        for(int i = 0; i < crc16.length; i++)
            frameBytes[i+Frame.CRC] = crc16[i];
    }
    public Frame stamp(byte seq){
        frameBytes[Frame.SEQUENCE_NUMBER] = seq;
        return this;
    }
    public boolean valid(){
        return CRC16.valid(Arrays.copyOfRange(frameBytes,Frame.DATA,Frame.END));
    }
    public byte[] getFrameBytes()
    {
        return this.frameBytes;
    }
    public int getSequenceNumber() {
        return (int) this.frameBytes[Frame.SEQUENCE_NUMBER];
    }

    @Override
        public String toString() {
        return "seq: " + getSequenceNumber() + " data: " + this.frameBytes;
    }
}