import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.PrintStream;
public class Channel{
    private BlockingQueue<Byte>q =
        new LinkedBlockingQueue<Byte>();
    private ErrorModel errorModel;
    private PrintStream log;
    public Channel(ErrorModel errorModel,PrintStream log){
        this.errorModel = errorModel;
        this.log = log;
    }
    public void sendByte(Byte b){
        try{
            q.put(b);
        }catch (InterruptedException e){
            System.err.println("Fatal Error: - Channel interrupted during write");
            System.exit(1);
        }
        if (log != null)
            log.println("Channel transmit: " + Datalinklayer.getBinaryString(b.byteValue()));
    }
    public Byte recvByte(){

        Byte b = null, c = null;

        try{
            b = q.take();
        }catch(InterruptedException e){
            System.err.println("Fatal Error: Channel interrupted during read");
            System.exit(-1);
        }
        String logMessage = "Channel receives " + Datalinklayer.getBinaryString(b.byteValue())+ " as ";
        if((c = errorModel.mangle(b)) != null){
            if (b.byteValue() != c.byteValue())
                logMessage = "\t" + logMessage + Datalinklayer.getBinaryString(c);
            else
                logMessage += Datalinklayer.getBinaryString(c);
        }else
            logMessage = "\t" + logMessage  + "\t<LOST>";

        if (log != null)
            log.println(logMessage);
        return c;
    }
    public int bytesInChannel() {return q.size();}
}