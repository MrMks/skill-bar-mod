package com.github.MrMks.skillbar.forge.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.PartMerge;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.enums.CleanType;
import com.github.MrMks.skillbar.common.handler.IClientHandler;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.forge.BarControl;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.MrMks.skillbar.forge.pkg.PackageSender.send;

/**
 * This class response to handle messages from server plugin;
 * and methods under this class may be all running in network thread
 * Operation under this class should all consider about thread safety
 */
public class PackageHandler implements IMessageHandler<PackageMessage, IMessage>, IClientHandler {
    private final HashMap<Byte,PartMerge> partMap = new HashMap<>();

    private final BarControl control;
    public PackageHandler(BarControl control) {
        this.control = control;
    }

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
                case RemoveSkill:
                    SPackage.DECODER.decodeRemoveSkill(this,dec);
            }
        }
        return null;
    }

    @Override
    public void onDiscover() {
        onCleanUp(CleanType.ALL);
        onDisable();
        send(CPackage.BUILDER.buildDiscover());
    }

    @Override
    public void onSetting(int maxSize, List<Integer> list) {
        control.getServerSetting().setMaxPage(maxSize);
        control.getSkillStore().setSlots(list);
    }

    @Override
    public void onEnable() {
        if (control.isValid()) {
            control.onEnable();
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("msg.skillbar.enable"))));
        }
    }

    @Override
    public void onCleanUp(CleanType type) {
        switch (type) {
            case ALL:
                control.cleanSetting();
            case SKILL_BAR:
                control.cleanSkills();
            case BAR:
                control.cleanBars();
                break;
            default:
                switch (type) {
                    case SETTING_SKILL:
                        control.cleanSetting();
                    case SKILL:
                        control.cleanSkills();
                        break;
                    default:
                        switch (type) {
                            case SETTING_BAR:
                                control.cleanBars();
                            case SETTING:
                                control.cleanSetting();
                        }
                }
        }
    }

    @Override
    public void onDisable(){
        if (control.isEnable())
            Minecraft.getMinecraft().addScheduledTask(()-> Minecraft.getMinecraft().player.sendMessage(new TextComponentString(I18n.format("msg.skillbar.disable"))));
        control.onDisable();
    }

    @Override
    public void onListSkill(List<SkillInfo> aList, List<String> rList) {
        control.getSkillStore().addSkills(aList);
        control.getSkillStore().removeSkills(rList);
    }

    @Override
    public void onAddSkill(List<SkillInfo> list) {
        control.getSkillStore().addSkills(list);
    }

    @Override
    public void onRemoveSkill(List<String> list) {
        control.getSkillStore().removeSkills(list);
    }

    @Override
    public void onUpdateSkill(SkillInfo info) {
        control.getSkillStore().updateSkill(info);
    }

    @Override
    public void onListBar(Map<Integer, String> map) {
        control.getSkillStore().setBarSkills(map);
    }

    @Override
    public void onCoolDown(Map<String, Integer> map) {
        control.getSkillStore().setCooldownMap(map);
    }

}
