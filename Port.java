public class Port
{
    protected Channel outgoing;
    protected Channel incoming;
    public Port( Channel outgoing, Channel incoming )
    {
        this.outgoing = outgoing;
        this.incoming = incoming;
    }
    public void sendFrame(byte[]data){
        for(byte datum : data)
            outgoing.sendByte(new Byte(datum));
    }
    public int recvFrame(byte[]data){
        int index = 0;
        while(index < Frame.SIZE)
            data[index++] = this.incoming.recvByte().byteValue();
        return index;
    }
    public boolean hasMessageWaiting ()
    {
        return this.incoming.bytesInChannel() > 0;
    }
}