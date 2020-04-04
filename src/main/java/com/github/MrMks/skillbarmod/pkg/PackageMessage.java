package com.github.MrMks.skillbarmod.pkg;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PackageMessage implements IMessage {
    private ByteBuf buf;
    @Override
    public void fromBytes(ByteBuf buf) {
        this.buf = buf.copy();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBuf mBuf = this.buf;
        while (mBuf.readableBytes() > 0){
            if (buf.writableBytes() == 0) buf.capacity(buf.capacity() + mBuf.readableBytes());
            mBuf.readBytes(buf,Math.min(mBuf.readableBytes(), buf.writableBytes()));
        }
    }

    ByteBuf getBuf(){
        return buf;
    }

    void setBuf(ByteBuf buf){
        this.buf = buf;
    }

}
