import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Printthis{
    public static void main(String[]args) throws FileNotFoundException{
    	boolean ackframe=true;
    	int sequencenum=1;
        Frame frame = null;
        Frame frame2=null;
        Frame frame3=null;
        String data="give everyon echan";
        String data2="e a chance t";
        String data3="o speak";
        System.out.println("data = "+data);
        frame = new Frame(data.getBytes());
        frame2= new Frame(data2.getBytes());
        frame3= new Frame(data3.getBytes());
        frame = new Frame(new byte[]{(byte)0});
        frame2= new Frame(new byte[]{(byte)0});
        frame3=new Frame(new byte[]{(byte)0});
        if(frame.valid())
            System.out.println("null frame valid");
        else
            System.out.println("null frame invalid");
        ErrorModel senderchannelerror=new ErrorModel(0);
        ErrorModel recieverchannelerror=new ErrorModel(0);
        PrintStream dataprinted=new PrintStream(data);
        Channel senderchannel=new Channel(senderchannelerror, dataprinted);
        Channel recieverchannel=new Channel(recieverchannelerror, dataprinted);
        Port senderport=new Port(senderchannel, recieverchannel);
        Port recieverport=new Port(recieverchannel, senderchannel);
        senderport.sendFrame(data.getBytes());
        recieverport.recvFrame(data.getBytes());
        Sender mysender=new Sender(data, senderport);
        Receiver myreciever=new Receiver(data, recieverport);
        mysender.sendFrame(frame);
        myreciever.makeReplyFrame(ackframe, sequencenum);
        myreciever.run();
        mysender.sendFrame(frame2);
        myreciever.makeReplyFrame(ackframe, sequencenum);
        myreciever.run();
        mysender.sendFrame(frame3);
        myreciever.makeReplyFrame(ackframe, sequencenum);
        myreciever.run();
        System.out.println(""+myreciever.toString());
    }
}
