package com.github.MrMks.skillbarmod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ByteDecoder {
    private Charset utf8 = StandardCharsets.UTF_8;
    private ByteBuf buf;
    private byte header;
    public ByteDecoder(byte[] bytes) throws IndexOutOfBoundsException{
        buf = PooledByteBufAllocator.DEFAULT.buffer(bytes.length - 1);
        buf.writeBytes(bytes,1,bytes.length - 1);
        header = buf.readByte();
    }

    public ByteDecoder(ByteBuf buf){
        this.buf = buf;
        header = buf.readByte();
    }

    public byte getHeader(){
        return header;
    }

    public byte read() throws IndexOutOfBoundsException{
        return buf.readByte();
    }

    public short readShort() throws IndexOutOfBoundsException{
        return buf.readShort();
    }

    public int readInt() throws IndexOutOfBoundsException{
        return buf.readInt();
    }

    public long readLong() throws IndexOutOfBoundsException{
        return buf.readLong();
    }

    public boolean readBoolean() throws IndexOutOfBoundsException{
        return buf.readBoolean();
    }

    public CharSequence readCharSequence() throws IndexOutOfBoundsException{
        return buf.readCharSequence(readInt(),utf8);
    }

    public List<CharSequence> readCharSequenceList() throws IndexOutOfBoundsException{
        List<CharSequence> list = new ArrayList<>();
        int size = readInt();
        for (int i = 0; i < size; i++){
            list.add(readCharSequence());
        }
        return list;
    }
}