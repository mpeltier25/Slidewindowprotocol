import java.io.FileNotFoundException;
import java.io.FileReader;

public class Sender extends Thread {
    private Port port;
    private String fileName;
    private Slidewindowprotocol window = new Slidewindowprotocol(Datalinklayer.WINDOW_SIZE);
    public Sender(String filename, Port port)
    {
        this.port = port;
        this.fileName = filename;
    }
    public void sendFrame(Frame aFrame)
    {
        this.port.sendFrame(aFrame.getFrameBytes());
        this.window.addSentFrame(new sendingframe(aFrame));
    }
    @Override
    public void run(){

        FileReader fr = null;
        try{
            fr = new FileReader(this.fileName);
        }catch(FileNotFoundException fnfe){
            System.err.println("Cannot find file: " + this.fileName);
            System.exit(-1);
        }
        Framecreator frameFactory = new Framecreator(fr);
        Frame nextFrame = frameFactory.next();
        while (nextFrame != null  || ! this.window.equals(null)) {
            while (! this.window.isFull() && nextFrame != null) {
                sendFrame(nextFrame);
                System.out.println("Sent " + nextFrame.getSequenceNumber());
                nextFrame = frameFactory.next();
            }
            while (port.hasMessageWaiting()) {
                byte[] frame = new byte [Frame.SIZE];
                int numBytes = port.recvFrame(frame);
                int seqNumber = frame[Frame.SEQUENCE_NUMBER];
                int msg = frame[Frame.DATA];
                if (msg == (byte) Frame.ACK) {
                    System.out.println("Recieved ACK for  " + seqNumber);
                    this.window.acknowledgeFrame(seqNumber);
                }
                else {
                    System.out.println("Recieved NACK for  " + seqNumber);
                    sendFrame(this.window.getFrame(seqNumber));
                }
            }
            if (this.window.hasFrameTimeouts()) {
                for (sendingframe frame : this.window.getAllFrames())
                    sendFrame(frame.getFrame());
                break;
                }
        }
    }
}