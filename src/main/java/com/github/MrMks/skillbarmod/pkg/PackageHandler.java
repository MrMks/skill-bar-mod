package com.github.MrMks.skillbarmod.pkg;

import com.github.MrMks.skillbarmod.ByteDecoder;
import com.github.MrMks.skillbarmod.PartMerge;
import com.github.MrMks.skillbarmod.skill.Manager;
import com.github.MrMks.skillbarmod.skill.SkillInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.MrMks.skillbarmod.Constants.*;

/**
 * This class response to handle messages from server plugin;
 * and methods under this class may be all running in network thread
 * Operation under this class should all consider about thread safety
 */
public class PackageHandler implements IMessageHandler<PackageMessage, IMessage> {
    private HashMap<Byte,PartMerge> partMap = new HashMap<>();

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
                case ENABLE:
                    onEnable(dec);
                    break;
                case ACCOUNT:
                    onAccount(dec);
                    break;
                case DISABLE:
                    onDisable(dec);
                    break;
                case ENFORCE_LIST_SKILL:
                    onEnforceListSkill(dec);
                    break;
                case LIST_SKILL:
                    onListSkill(dec);
                    break;
                case ENFORCE_UPDATE_SKILL:
                    onEnforceUpdateSKill(dec);
                    break;
                case UPDATE_SKILL:
                    onUpdateSkill(dec);
                    break;
                case CAST:
                    onCast(dec);
                    break;
                case COOLDOWN:
                    onCoolDown(dec);
                    break;
                case ADD_SKILL:
                    onAddSkill(dec);
                    break;
                case LIST_BAR:
                    onListBar(dec);
            }
        }
        return null;
    }

    private void onEnable(ByteDecoder dec){
        int activeId = dec.readInt();
        int skillSize = dec.readInt();
        Manager manager;
        synchronized (manager = Manager.prepareManager(activeId)) {
            if (manager.getId() < 0) Manager.setActiveId(-1); //this should never happen as account id is only allowed to be 1 to n;
            else {
                Manager.setActiveId(activeId);
                Manager.setEnable(true);
                if (manager.isListSkill(skillSize)) PackageSender.sendListSkill(manager.getSkillKeyList());
                if (manager.isListBar()) PackageSender.sendListBar();
            }
        }
        PackageSender.sendDiscover();
        Minecraft.getMinecraft().addScheduledTask(()-> Minecraft.getMinecraft().player.sendMessage(new TextComponentString("\u00A72" + I18n.format("msg.skillbar.enable"))));
    }

    private void onAccount(ByteDecoder dec){
        int activeId = dec.readInt();
        int skillSize = dec.readInt();
        Manager manager;
        synchronized (manager = Manager.prepareManager(activeId)){
            if (!manager.isActive()) return;
            Manager.setActiveId(activeId);
            if (manager.isListSkill(skillSize)) PackageSender.sendListSkill(manager.getSkillKeyList());
            if (manager.isListBar()) PackageSender.sendListBar();
        }
    }

    private void onDisable(ByteDecoder dec){
        if (Manager.isEnable()){
            Manager.setEnable(false);
            Minecraft.getMinecraft().addScheduledTask(()-> Minecraft.getMinecraft().player.sendMessage(new TextComponentString("\u00A72" + I18n.format("msg.skillbar.disable"))));
        }
    }

    private void onListSkill(ByteDecoder dec){
        Manager manager;
        synchronized (manager = Manager.getManager()){
            if (manager.isActive()) manager.setSkillMap(readSkillInfoList(dec), dec.readCharSequenceList());
        }
    }

    private void onEnforceListSkill(ByteDecoder dec){
        int activeId = dec.readInt();
        Manager manager = Manager.getManager(activeId);
        if (manager != null && manager.getId() > 0){
            manager.setSkillMap(readSkillInfoList(dec));
        }
    }

    private void onUpdateSkill(ByteDecoder dec){
        String key = dec.readCharSequence().toString();
        boolean exist = dec.readBoolean();
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            if (exist) {
                SkillInfo info = new SkillInfo(key, dec.readBoolean(), dec.readBoolean(), readItemStack(dec));
                manager.addSkill(info);
            } else {
                manager.removeSkill(key);
            }
        }
    }

    private void onEnforceUpdateSKill(ByteDecoder dec){
        int activeId = dec.readInt();
        SkillInfo info = readSkillInfo(dec);
        Manager manager = Manager.getManager(activeId);
        if (manager != null) manager.addSkill(info);
    }

    private void onCast(ByteDecoder dec){
        String key = dec.readCharSequence().toString();
        boolean exist = dec.readBoolean();
        if (exist){
            dec.readBoolean();
        } else {
            Manager manager = Manager.getManager();
            if (manager.isActive()) manager.removeSkill(key);
        }
    }

    private void onCoolDown(ByteDecoder dec){
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            long time = dec.readLong();
            long now = System.currentTimeMillis();
            int size = dec.readInt();
            HashMap<String, Integer> map = new HashMap<>(size);
            for (int index = 0; index < size; index ++){
                String key = dec.readCharSequence().toString();
                int coolDown = dec.readInt();
                coolDown = coolDown - (int)((now - time) / 1000L);
                map.put(key,coolDown);
            }
            manager.setCoolDown(map);
        }
    }

    private void onAddSkill(ByteDecoder dec){
        //if (!isEnable()) return;
        onAccount(dec);
    }

    private void onListBar(ByteDecoder dec){
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            boolean exist = dec.readBoolean();
            if (exist) {
                int size = dec.readInt();
                HashMap<Integer, String> map = new HashMap<>();
                for (int i = 0; i < size; i++){
                    map.put(dec.readInt(), dec.readCharSequence().toString());
                }
                manager.setBarMap(map);
            }
        }
    }

    private ArrayList<SkillInfo> readSkillInfoList(ByteDecoder dec){
        int size = dec.readInt();
        ArrayList<SkillInfo> list = new ArrayList<>(size);
        for (int index = 0; index < size; index ++){
            list.add(readSkillInfo(dec));
        }
        return list;
    }

    private SkillInfo readSkillInfo(ByteDecoder dec){
        String key = dec.readCharSequence().toString();
        boolean unlock = dec.readBoolean();
        boolean canCast = dec.readBoolean();
        ItemStack stack = readItemStack(dec);
        return new SkillInfo(key, unlock, canCast, stack);
    }

    private ItemStack readItemStack(ByteDecoder dec){
        int itemId = dec.readInt();
        int amount = 1;
        short damage = dec.readShort();
        NBTTagCompound display = new NBTTagCompound();
        display.setTag("Name", new NBTTagString(dec.readCharSequence().toString()));
        NBTTagList loreList = new NBTTagList();
        List<CharSequence> lores = dec.readCharSequenceList();
        for (CharSequence sequence : lores){
            loreList.appendTag(new NBTTagString(sequence.toString()));
        }
        display.setTag("Lore", loreList);
        ItemStack stack = new ItemStack(Item.getItemById(itemId),amount,damage);
        stack.setTagInfo("display", display);
        return stack;
    }

}
