package com.github.MrMks.skillbarmod.common.pkg;

import com.github.MrMks.skillbarmod.common.ByteDecoder;
import com.github.MrMks.skillbarmod.common.handler.IServerHandler;

public interface IDecoderCP {
    void decodeDiscover(IServerHandler handler, ByteDecoder decoder);
    void decodeListSkill(IServerHandler handler, ByteDecoder decoder);
    void decodeUpdateSkill(IServerHandler handler, ByteDecoder decoder);
    void decodeListBar(IServerHandler handler, ByteDecoder decoder);
    void decodeSaveBar(IServerHandler handler, ByteDecoder decoder);
    void decodeCast(IServerHandler handler, ByteDecoder decoder);
}
