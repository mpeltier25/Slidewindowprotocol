import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Receiver extends Thread
{
    private String fileName;
    private Port port;
    private ArrayList<Byte> bytesReceived = new ArrayList<Byte>();
    private int framesReceived = 0;
    private int acksSent = 0;
    private int nacksSent = 0;
    private int totalBytesReceived = 0;

    public Receiver(String filename, Port aPort)
    {
        this.fileName = filename;
        this.port = aPort;
    }
    private BigInteger bitUnstuff(BigInteger stuffedData)
    {
        BigInteger out = BigInteger.ZERO;
        int i = 0, j = 0, count = 0;
        while(i < stuffedData.bitLength()){
            if(stuffedData.testBit(i++)){
                out = out.setBit(j++);
                if(++count == 5){
                    i++;
                    count = 0;
                }
            }else{
                out = out.clearBit(j++);
                count = 0;
            }
        }
        return out;
    }
    public byte[] makeReplyFrame(boolean ack, int seqNum)
    {
        byte[] frameData = new byte[Frame.PAYLOAD];
        Arrays.fill(frameData, (byte) 0);
        if (ack){
            frameData[0] = (byte) Frame.ACK;
            this.acksSent++;
        }else {
            frameData[0] = (byte) Frame.NACK;
            this.nacksSent ++;
        }
        byte[] frame = new byte[Frame.SIZE];
        frame[Frame.START] = Frame.FRAMING_BYTE;
        frame[Frame.MESSAGE_ID] = (byte) seqNum;
        frame[Frame.SEQUENCE_NUMBER] = (byte) seqNum;
        for (int i = 0; i < Frame.PAYLOAD; i ++ )
            frame[Frame.DATA + i] = frameData[i];
        byte[] checkSum = CRC16.go(frameData);
        frame[Frame.CRC] = checkSum[0];
        frame[Frame.CRC + 1] = checkSum[1];
        frame[Frame.END] = Frame.FRAMING_BYTE;
        System.out.println("\t\tSent " + ((ack) ? "ACK" : "NACK") + ": " + seqNum);
        return frame;
    }

    private boolean isLastFrame(byte[] frame)
    {
        for (int b = 0; b < Frame.PAYLOAD; b++)
            if (frame[Frame.DATA + b] != (byte) 0)
                return false;
        return true;
    }
    @Override
    public void run()
    {
        int nextSeq = 0;
        int lastReceived = -1;
        while (true) {
            byte[] frameBytes = new byte[Frame.SIZE];
            int byteCount = port.recvFrame(frameBytes);
            int sequenceNumber = (int) frameBytes[Frame.SEQUENCE_NUMBER];
            if (sequenceNumber == nextSeq) {
                boolean valid = CRC16.valid(Arrays.copyOfRange(frameBytes, Frame.DATA, Frame.END));
                if (valid) {
                    nextSeq ++;
                    lastReceived ++;
                    this.framesReceived ++;
                    System.out.println("\t\tReceived: " + sequenceNumber);
                    for (int b = Frame.PAYLOAD - 1; b >= 0; b--)
                        this.bytesReceived.add(frameBytes[Frame.DATA + b]);
                }
                this.port.sendFrame(makeReplyFrame(valid, sequenceNumber));
            }
            else
                this.port.sendFrame(makeReplyFrame(true, lastReceived));
            if (isLastFrame(frameBytes))
                break;
        }
        byte[] stuffedBytes = new byte[this.bytesReceived.size()];
        for (int b = 0; b < this.bytesReceived.size(); b++)
            stuffedBytes[b] = this.bytesReceived.get(b).byteValue();
        BigInteger stuffed = new BigInteger(stuffedBytes);
        byte[] unstuffedBytes = bitUnstuff(stuffed).toByteArray();
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(this.fileName));
        } catch (IOException ex) {
            System.err.println("Error opening output file: " + this.fileName);
            System.exit(-1);
        }
        for (int b = 0; b < unstuffedBytes.length; b++)
            if (unstuffedBytes[b] != (byte) 0) {
                this.totalBytesReceived ++;
                System.out.print((char)unstuffedBytes[b]);
                try {
                    writer.write(unstuffedBytes[b]);
                } catch (IOException ex) {
                    System.err.println("Error writing to output file: " + this.fileName);
                    System.exit(-1);
                }

            }
    }
    @Override
    public String toString()
    {
        return "Receiver Statistics:  \n" +
                "\t total amount of data transmitted:  " + this.framesReceived + "\n" +
                "\t totalnumber of datapackets Sent:  " + this.acksSent + "\n" +
                "\tcorrupted frames Sent:  " + this.nacksSent + "\n";
    }
}