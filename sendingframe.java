public class sendingframe
{
    private Frame frame;
    private Frametimeout timer;
	public sendingframe(Frame aFrame)
	{
        this.frame = aFrame;
        this.timer = new Frametimeout(aFrame.getSequenceNumber());
	}
    public void startTimer()
    {
        this.timer.start();
    }
    public void stopTimer()
    {
        this.timer.stop();
    }
    public boolean isTimerExpired()
    {
        return this.timer.getExpired();
    }
    public Frame getFrame()
    {
        return this.frame;
    }
    public int getSequenceNumber()
    {
        return this.frame.getSequenceNumber();
    }
}