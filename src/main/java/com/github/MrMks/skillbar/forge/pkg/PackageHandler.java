package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.forge.ConditionManager;
import com.github.MrMks.skillbar.forge.GameSetting;
import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.PartMerge;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.handler.IClientHandler;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.forge.skill.Manager;
import com.github.MrMks.skillbar.forge.skill.ForgeSkillInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.*;

import static com.github.MrMks.skillbar.common.Constants.*;

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
                case DISCOVER:
                    SPackage.DECODER.decodeDiscover(this,dec);
                    //onDiscover(dec);
                    break;
                case ENABLE:
                    SPackage.DECODER.decodeEnable(this,dec);
                    //onEnable(dec);
                    break;
                case SETTING:
                    SPackage.DECODER.decodeSetting(this,dec);
                    //onSetting(dec);
                    break;
                case ACCOUNT:
                    SPackage.DECODER.decodeAccount(this,dec);
                    // onAccount(dec);
                    break;
                case DISABLE:
                    SPackage.DECODER.decodeDisable(this,dec);
                    // onDisable();
                    break;
                case ENFORCE_LIST_SKILL:
                    SPackage.DECODER.decodeEnforceListSkill(this,dec);
                    //onEnforceListSkill(dec);
                    break;
                case LIST_SKILL:
                    SPackage.DECODER.decodeListSkill(this,dec);
                    //onListSkill(dec);
                    break;
                case ENFORCE_UPDATE_SKILL:
                    SPackage.DECODER.decodeEnforceUpdateSkill(this,dec);
                    //onEnforceUpdateSKill(dec);
                    break;
                case UPDATE_SKILL:
                    SPackage.DECODER.decodeUpdateSkill(this,dec);
                    //onUpdateSkill(dec);
                    break;
                case CAST:
                    SPackage.DECODER.decodeCast(this,dec);
                    //onCast(dec);
                    break;
                case COOLDOWN:
                    SPackage.DECODER.decodeCoolDown(this,dec);
                    //onCoolDown(dec);
                    break;
                case ADD_SKILL:
                    SPackage.DECODER.decodeAddSkill(this,dec);
                    //onAddSkill(dec);
                    break;
                case LIST_BAR:
                    SPackage.DECODER.decodeListBar(this,dec);
                    //onListBar(dec);
                    break;
                case CLEAN:
                    SPackage.DECODER.decodeCleanUp(this,dec);
                    //onClean(dec);
                    break;
                case ENTER_CONDITION:
                    SPackage.DECODER.decodeEnterCondition(this,dec);
                    break;
                case LEAVE_CONDITION:
                    SPackage.DECODER.decodeLeaveCondition(this,dec);
                    break;
            }
        }
        return null;
    }

    @Override
    public void onDiscover(int version) {
        if (version == VERSION) PackageSender.send(CPackage.BUILDER.buildDiscover(ForgeByteBuilder::new));
    }

    @Override
    public void onSetting(int maxSize) {
        GameSetting.getInstance().setMaxBarPage(maxSize);
    }

    private final Queue<ByteBuilder> queue = new LinkedList<>();
    @Override
    public void onEnable() {
        if (!Manager.isEnable()) {
            Manager.setEnable(true);
            while (!queue.isEmpty()) PackageSender.send(queue.poll());
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("msg.skillbar.enable"))));
        }
    }

    @Override
    public void onAccount(int activeId, int skillSize) {
        Manager manager;
        Manager.setActiveId(activeId);
        synchronized (manager = Manager.prepareManager(activeId)){
            queue.clear();
            if (manager.isListSkill(skillSize)) queue.add(CPackage.BUILDER.buildListSkill(ForgeByteBuilder::new,manager.getSkillKeyList()));
            if (manager.isListBar() || conditionSwitch) queue.add(CPackage.BUILDER.buildListBar(ForgeByteBuilder::new));
            if (Manager.isEnable()) while (!queue.isEmpty()) PackageSender.send(queue.poll());
            conditionSwitch = false;
        }
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
    public void onListSkill(List<SkillInfo> aList, List<CharSequence> reList) {
        Manager manager;
        ArrayList<ForgeSkillInfo> infos = new ArrayList<>();
        for (SkillInfo info : aList) infos.add(new ForgeSkillInfo(info));
        synchronized (manager = Manager.getManager()){
            if (manager.isActive()) manager.setSkillMap(infos, reList);
        }
    }

    @Override
    public void onEnforceListSkill(int id, List<SkillInfo> list) {
        Manager manager = Manager.getManager(id);
        if (manager != null && manager.getId() > 0){
            ArrayList<ForgeSkillInfo> infos = new ArrayList<>();
            for (SkillInfo info : list) infos.add(new ForgeSkillInfo(info));
            manager.setSkillMap(infos);
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
    public void onEnforceUpdateSKill(int id, SkillInfo info) {
        if (info.isExist()) {
            Manager manager = Manager.getManager(id);
            if (manager != null && manager.getId() > 0) manager.addSkill(new ForgeSkillInfo(info));
        }
    }

    @Override
    public void onAddSkill(List<SkillInfo> list) {
        onListSkill(list, Collections.emptyList());
    }

    @Override
    public void onListBar(Map<Integer, CharSequence> map) {
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            Map<Integer,String> nMap = new HashMap<>();
            for (Map.Entry<Integer, CharSequence> entry: map.entrySet()) nMap.put(entry.getKey(), entry.getValue().toString());
            manager.setBarMap(nMap,true);
        }
    }

    private boolean conditionSwitch = false;
    @Override
    public void onEnterCondition(CharSequence key, int size, boolean fix, boolean free, List<Integer> freeSlots) {
        ConditionManager.enter(key.toString(),size,fix, free, freeSlots);
        conditionSwitch = true;
    }

    @Override
    public void onLeaveCondition(CharSequence key) {
        ConditionManager.leave();
        conditionSwitch = true;
    }

    @Override
    @Deprecated
    public void onFixBar(boolean fix) {
        //GameSetting.getInstance().setFixBar(fix);
        //if (!fix) Manager.clearFreeList();
    }

    @Override
    @Deprecated
    public void onFreeSlot(List<Integer> list) {
        //Manager.setFreeList(list);
    }

    @Override
    public void onCast(CharSequence key, boolean exist, boolean suc, byte code) {
        if (!suc){
            switch (code) {
                case CAST_FAILED_NO_SKILL:
                    Manager manager = Manager.getManager();
                    if (manager.isActive()) manager.removeSkill(key.toString());
                    break;
                case CAST_FAILED_UNLOCK:
                case CAST_FAILED_COOLDOWN:
                    break;
                default:
            }
        }
    }

    @Override
    public void onCoolDown(Map<CharSequence, Integer> map) {
        Manager manager = Manager.getManager();
        if (manager.isActive()){
            HashMap<String, Integer> nMap = new HashMap<>();
            for (Map.Entry<CharSequence, Integer> entry : map.entrySet()) nMap.put(entry.getKey().toString(), entry.getValue());
            manager.setCoolDown(nMap);
        }
    }

}
