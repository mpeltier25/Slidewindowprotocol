import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Frametimeout implements ActionListener
{
    private Timer timer = new Timer(1000, this);
    private int sequenceNumber;
    private boolean expired = false;
    public Frametimeout(int seqNum)
    {
        this.sequenceNumber = seqNum;
        this.timer.setRepeats(false);
    }
    public void start()
    {
        if (timer.isRunning())
            timer.stop();
        timer.start();
        this.expired = false;
    }
    public void stop()
    {
        timer.stop();
        this.expired = false;
    }
    public void restart()
    {
        timer.restart();
        this.expired = false;
    }
    public boolean getExpired()
    {
        return this.expired;
    }
    public void actionPerformed( ActionEvent e )
    {
        if (e.getSource() == timer)
        {
            this.expired = true;
            System.out.println("Packet " + this.sequenceNumber + " timed out.");
            this.timer.stop();
        }
    }
}
