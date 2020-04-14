package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import io.netty.buffer.ByteBuf;

public class ForgeByteBuilder extends ByteBuilder {
    public ForgeByteBuilder(byte header) {
        super(header);
    }

    @Override
    public byte[][] build(byte partId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuf buildBuf() {
        return getBuf();
    }
}