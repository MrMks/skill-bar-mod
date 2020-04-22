package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.PartMerge;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.handler.IClientHandler;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.forge.setting.ClientSetting;
import com.github.MrMks.skillbar.forge.setting.ServerSetting;
import com.github.MrMks.skillbar.forge.skill.Condition;
import com.github.MrMks.skillbar.forge.skill.ForgeSkillInfo;
import com.github.MrMks.skillbar.forge.skill.Manager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.*;

/**
 * This class response to handle messages from server plugin;
 * and methods under this class may be all running in network thread
 * Operation under this class should all consider about thread safety
 */
public class PackageHandler implements IMessageHandler<PackageMessage, IMessage>, IClientHandler {
    private final HashMap<Byte,PartMerge> partMap = new HashMap<>();

    @Override
    public IMessage onMessage(PackageMessage message, MessageContext ctx) {
        ByteBuf buf = message.getBuf();
        if (buf == null) return null;
        else {
            byte req = buf.getByte(0);
            if (partMap.containsKey(req)) {
                PartMerge merge = partMap.get(req);
                merge.addPart(buf);
                if (merge.isComplete()){
                    return onFullPackage(partMap.remove(req).getFullPackage());
                }
            } else {
                PartMerge merge = new PartMerge(req, buf.getByte(1));
                merge.addPart(buf);
                if (merge.isComplete()) return onFullPackage(merge.getFullPackage());
                else partMap.put(req,merge);
            }
        }
        return null;
    }

    private IMessage onFullPackage(ByteBuf buf) {
        if (buf != null){
            ByteDecoder dec = new ByteDecoder(buf);
            switch (dec.getHeader()){
                case Discover:
                    SPackage.DECODER.decodeDiscover(this,dec);
                    //onDiscover(dec);
                    break;
                case Enable:
                    SPackage.DECODER.decodeEnable(this,dec);
                    //onEnable(dec);
                    break;
                case Setting:
                    SPackage.DECODER.decodeSetting(this,dec);
                    //onSetting(dec);
                    break;
                case Account:
                    SPackage.DECODER.decodeAccount(this,dec);
                    // onAccount(dec);
                    break;
                case Disable:
                    SPackage.DECODER.decodeDisable(this,dec);
                    // onDisable();
                    break;
                case ListSkill:
                    SPackage.DECODER.decodeListSkill(this,dec);
                    //onListSkill(dec);
                    break;
                case UpdateSkill:
                    SPackage.DECODER.decodeUpdateSkill(this,dec);
                    //onUpdateSkill(dec);
                    break;
                case Cast:
                    SPackage.DECODER.decodeCast(this,dec);
                    //onCast(dec);
                    break;
                case CoolDown:
                    SPackage.DECODER.decodeCoolDown(this,dec);
                    //onCoolDown(dec);
                    break;
                case AddSkill:
                    SPackage.DECODER.decodeAddSkill(this,dec);
                    //onAddSkill(dec);
                    break;
                case ListBar:
                    SPackage.DECODER.decodeListBar(this,dec);
                    //onListBar(dec);
                    break;
                case Clean:
                    SPackage.DECODER.decodeCleanUp(this,dec);
                    //onClean(dec);
                    break;
                case EnterCondition:
                    SPackage.DECODER.decodeEnterCondition(this,dec);
                    break;
                case LeaveCondition:
                    SPackage.DECODER.decodeLeaveCondition(this,dec);
                    break;
                case RemoveSkill:
                    SPackage.DECODER.decodeRemoveSkill(this,dec);
            }
        }
        return null;
    }

    @Override
    public void onDiscover(int version) {
        PackageSender.send(CPackage.BUILDER.buildDiscover());
    }

    @Override
    public void onSetting(int maxSize) {
        ServerSetting.buildDefault(maxSize);
    }

    @Override
    public void onEnable() {
        if (!Manager.isEnable()) {
            Manager.setEnable(true);
            Manager manager;
            synchronized (manager = Manager.getManager()) {
                if (manager.isListSkill()) PackageSender.send(CPackage.BUILDER.buildListSkill(manager.getSkillKeyList()));
                if (manager.isListBar()) PackageSender.send(CPackage.BUILDER.buildListBar());
                if (manager.isActive()) {
                    Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("msg.skillbar.enable"))));
                }
            }
        }
    }

    @Override
    public void onAccount(int activeId) {
        Manager.setActiveId(activeId);
        Manager.prepareManager(activeId);
    }

    @Override
    public void onCleanUp(int activeId) {
        Manager manager = Manager.getManager(activeId);
        manager.clear();
    }

    @Override
    public void onDisable(){
        if (Manager.isEnable()){
            Manager.setEnable(false);
            Minecraft.getMinecraft().addScheduledTask(()-> Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("msg.skillbar.disable"))));
        }
    }

    @Override
    public void onListSkill(List<SkillInfo> aList) {
        Manager manager;
        ArrayList<ForgeSkillInfo> infos = new ArrayList<>();
        for (SkillInfo info : aList) infos.add(new ForgeSkillInfo(info));
        synchronized (manager = Manager.getManager()){
            manager.setSkillMap(infos);
        }
    }

    @Override
    public void onAddSkill(List<SkillInfo> list) {
        Manager manager;
        List<ForgeSkillInfo> infos = new ArrayList<>();
        list.forEach(info -> infos.add(new ForgeSkillInfo(info)));
        synchronized (manager = Manager.getManager()) {
            manager.setSkillMap(infos, Collections.emptyList());
        }
    }

    @Override
    public void onRemoveSkill(List<String> list) {
        Manager manager;
        synchronized (manager = Manager.getManager()) {
            manager.setSkillMap(Collections.emptyList(), list);
        }
    }

    @Override
    public void onUpdateSkill(SkillInfo info) {
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            if (info.isExist()) {
                manager.addSkill(new ForgeSkillInfo(info));
            } else manager.removeSkill(info.getKey());
        }
    }

    @Override
    public void onListBar(Map<Integer, String> map) {
        Manager manager;
        synchronized (manager = Manager.getManager()){
            manager.setBarMap(map,true);
        }
    }

    @Override
    public void onEnterCondition(int size, boolean fix, boolean free, Map<Integer, String> map, List<Integer> freeSlots) {
        ClientSetting.getInstance().setSize(0);
        Manager.enterCondition(new Condition(size,fix,free,map,freeSlots));
        ServerSetting.getInstance().setMaxSize(size);
    }

    @Override
    public void onLeaveCondition() {
        ClientSetting.getInstance().setSize(0);
        Manager.leaveCondition();
        ServerSetting.getInstance().setMaxSize(-1);
    }

    @Override
    public void onCast(String key, boolean exist, boolean suc) {
        if (!exist) Manager.getManager().removeSkill(key);
    }

    @Override
    public void onCoolDown(Map<String, Integer> map) {
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            HashMap<String, Integer> nMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) nMap.put(entry.getKey(), entry.getValue());
            manager.setCoolDown(nMap);
        }
    }

}
