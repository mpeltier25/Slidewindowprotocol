public class ErrorModel{
    private int counter = -1;
    private boolean
        perfect = false,
        noisy = false;
    public static final int PERFECT = 0;
    public static final int NOISY = 1;

    public static final String[] channelTypes = {"PERFECT", "NOISY"};
    public Byte mangle(Byte b){
        if(b == null)
            System.exit(-1);
        Byte c = new Byte(b.byteValue());
        if(++counter < 18)
            return c;
        if(perfect)
            return c;
        if(noisy)
            return (c = null);
        if(counter != 3)
            return c;
        c = new Byte((byte)(c.byteValue() ^ 1));

        return c;
    }
    public ErrorModel(int gotType){
        switch(gotType){
        case NOISY:
            noisy = true;
            break;
        default:
            perfect = true;
        }
    }
}