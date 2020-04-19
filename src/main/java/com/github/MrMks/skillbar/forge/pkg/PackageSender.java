package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PackageSender {
    private static SimpleNetworkWrapper w;
    public static void setWrapper(SimpleNetworkWrapper wrapper){
        w = wrapper;
    }

    public static void send(ByteBuilder builder){
        send(builder.buildBuf());
    }

    public static void send(ByteBuf buf){
        PackageMessage message = new PackageMessage();
        message.setBuf(buf);
        w.sendToServer(message);
    }
}
