package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.List;
import java.util.Map;

public class PackageSender {
    private static SimpleNetworkWrapper w;
    public static void setWrapper(SimpleNetworkWrapper wrapper){
        w = wrapper;
    }

    public static void sendDiscover(){
        ByteBuilder builder = new ForgeByteBuilder(Constants.DISCOVER);
        send(builder.buildBuf());
    }

    public static void sendCast(String name){
        ByteBuilder builder = new ForgeByteBuilder(Constants.CAST);
        builder.writeCharSequence(name);
        send(builder.buildBuf());
    }

    public static void sendListSkill(List<String> cached){
        ByteBuilder builder = new ForgeByteBuilder(Constants.LIST_SKILL);
        builder.writeCharSequenceList(cached);
        send(builder.buildBuf());
    }

    public static void sendUpdateSkill(String name){
        ByteBuilder builder = new ForgeByteBuilder(Constants.UPDATE_SKILL);
        builder.writeCharSequence(name);
        send(builder.buildBuf());
    }

    public static void sendListBar() {
        ByteBuilder builder = new ForgeByteBuilder(Constants.LIST_BAR);
        send(builder.buildBuf());
    }

    public static void sendSaveBar(int id, Map<Integer, String> map){
        ByteBuilder builder = new ForgeByteBuilder(Constants.SAVE_BAR);
        builder.writeInt(id)
                .writeInt(map.size());
        for (Map.Entry<Integer, String> entry : map.entrySet()){
            builder.writeInt(entry.getKey())
                    .writeCharSequence(entry.getValue());
        }
        send(builder.buildBuf());
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
