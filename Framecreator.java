import java.io.*;
import java.math.*;
import java.util.*;

public class Framecreator{
    private byte counter = 0;
    private Stack<Frame>frames = new Stack<Frame>();
    private Stack<Byte>byteStack(Reader r){
        try{
            int c;
            Stack<Byte>b = new Stack<Byte>();
            while( ( c = r.read() ) > -1){
                b.push((byte)c);
                b.push((byte)(c>>8));
            }
            return b;
        }catch(IOException ioe){
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }
        return null;
    }
    private byte[]byteArray(Stack<Byte>stack){
        byte[]ret = new byte[stack.size()];
        int i = 0;
        while(!stack.isEmpty())
            ret[i++] = stack.pop().byteValue();
        return ret;
    }
    private BigInteger bitStuff(BigInteger in){
        BigInteger out = BigInteger.ZERO;
        int i = 0, j = 0, count = 0;
        while(i < in.bitLength()){
            if(in.testBit(i++)){
                out = out.setBit(j++);
                if(++count == 5){
                    out = out.clearBit(j++);
                    count = 0;
                }
            }else{
                out = out.clearBit(j++);
                count = 0;
            }
        }
        return out;
    }
    private void frameThese(byte[]data){
        frames.push(new Frame(new byte[]{(byte)0}));
        int i = 0;
        do{
            frames.push(new Frame(Arrays.copyOfRange(data,i,i+Frame.PAYLOAD)));
        }while((i+=Frame.PAYLOAD) < data.length);
    }
    public Frame next(){
        if(frames.isEmpty())
            return null;
        return frames.pop().stamp(counter++);
    }
    public Framecreator(Reader r){
        BigInteger unstuffed = 
            new BigInteger(byteArray(byteStack(r)));
        BigInteger stuffed = bitStuff(unstuffed);
        frameThese(stuffed.toByteArray());
    }
}