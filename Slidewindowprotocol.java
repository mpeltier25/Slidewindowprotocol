import java.util.ArrayList;

public class Slidewindowprotocol
{
    private int windowSize;
    private ArrayList<sendingframe> frameList = new ArrayList<sendingframe>();
	public Slidewindowprotocol(int window)
	{
        this.windowSize = window;
	}
    public boolean isFull()
    {
        return (this.frameList.size() >= this.windowSize);
    }

    public void acknowledgeFrame(int sequenceNumber)
    {
        for (sendingframe frame : this.frameList)
            if (frame.getSequenceNumber() == sequenceNumber) {
                this.frameList.remove(frame);
                frame.stopTimer();
                break;
            }
    }

    public Frame getFrame(int sequenceNumber)
    {
        for (sendingframe frame : this.frameList)
            if (frame.getSequenceNumber() == sequenceNumber)
                return frame.getFrame();
        return null;
    }

    public boolean hasFrameTimeouts()
    {
        for (sendingframe frame : this.frameList)
            if (frame.isTimerExpired())
                return true;
        return false;
    }

    public ArrayList<sendingframe> getAllFrames()
    {
        return this.frameList;
    }

    public void addSentFrame(sendingframe aFrame)
    {
        aFrame.startTimer();
        this.frameList.add(aFrame);
    }

    public boolean containsFrameSeq(int sequenceNumber)
    {
        for (sendingframe frame : this.frameList)
            if (frame.getSequenceNumber() == sequenceNumber)
                return true;
        return false;
    }
}