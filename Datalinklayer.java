import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Datalinklayer{
    public static final int WINDOW_SIZE = 3;
    private Sender sender;
    private Receiver receiver;
    private Channel senderToReceiver,receiverToSender;
    private int errType;
    private String srcFile,dstFile,logFile;
    public static String getBinaryString(char aChar)
        {return padLeft(Integer.toBinaryString((int) aChar), 8, '0');}
    public static String getBinaryString(int aNum){return padLeft(Integer.toBinaryString(aNum), 8, '0');}
    public static String getBinaryString(byte[] buffer){
        String s = "";
        for (int i = 0; i < buffer.length; i++)
            s += padLeft(Integer.toBinaryString(buffer[i]), 8, '0');
        return s;
    }
    public static String padLeft(String s, int length, char pad)
    {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.length() < length)
            buffer.insert(0, pad);
        return buffer.toString();
    }
    public void startSim(){

        System.out.println("Sliding Window Simulation" +
                "\n\tWindow Size:  " + Datalinklayer.WINDOW_SIZE +
                "\n\tInput File:  " + this.srcFile +
                "\n\tOutput File:  " + this.dstFile +
                "\n\tChannel Log File:  " + this.logFile +
                "\n\tChannel Type:  " + ErrorModel.channelTypes[this.errType] + "\n");
        receiver.start();
        sender.start();

        try{
            sender.join();
        }catch(InterruptedException e){
            System.err.println("FATAL ERROR: - sender interrupted");
            System.exit(1);
        }
        try{
            receiver.join();
        }catch(InterruptedException e){
            System.err.println("FATAL ERROR:- receiver interrupted");
            System.exit(1);
        }
        System.out.println("\nSimulation Complete:");
        System.out.println(sender + "\n" +
                receiver);
    }
    public Datalinklayer(String[]args){

        errType = Integer.parseInt(args[0]);
        srcFile = args[1];
        dstFile = args[2];
        logFile = args[3];
        try{
            senderToReceiver =
                new Channel(
                            new ErrorModel(errType),
                            new PrintStream(new FileOutputStream(logFile))
                            );

            receiverToSender =
                new Channel(
                            new ErrorModel(ErrorModel.PERFECT),
                            null
                            );
        }catch(FileNotFoundException fnfe){
            System.err.println("Could not find logfile: "+logFile);
            System.exit(-1);
        }
        sender = new Sender(srcFile,new Port(receiverToSender,senderToReceiver));
        receiver = new Receiver(dstFile,new Port(senderToReceiver,receiverToSender));
    }
    public static void main(String[]args){
        if(args.length != 4)
            System.err.println("usage: DataLinkSimulation <error-type> <source-file> <dest-file> <log-file>");
        else
            new Datalinklayer(args).startSim();

        char c = 'a';
        byte b = (byte) c;
        Byte B = new Byte(b);

        Byte C = b;
        System.out.println(c+ ", " + b + ", " + B + ", " + C);
        
        System.exit(args.length-4);
    }
}