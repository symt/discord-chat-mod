package io.github.symt.commands;

import io.github.symt.DiscordChatMod;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class DList extends CommandBase {
    @Override
    public String getCommandName() {
        return "dlist";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dlist";
    }

    @Override
    public void processCommand(ICommandSender ics, String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) ics;
            JDA jda = DiscordChatMod.jda;
            List<Guild> guilds = jda.getGuilds();
            List<Member> allMembers = new ArrayList<>();
            for (Guild g : guilds) {
                List<Member> members = g.getMembers();
                for (Member m : members) {
                    if (!allMembers.contains(m)) {
                        allMembers.add(m);
                    }
                }
            }
            player.addChatMessage(DiscordChatMod.newLine);
            User u;
            for (Member m : allMembers) {
                u = m.getUser();
                String status;
                switch (m.getOnlineStatus()) {
                    case ONLINE:
                        status = EnumChatFormatting.GREEN + "Online";
                        break;
                    case OFFLINE:
                        status = EnumChatFormatting.RED + "Offline";
                        break;
                    case IDLE:
                        status = EnumChatFormatting.AQUA + "Idle";
                        break;
                    case DO_NOT_DISTURB:
                        status = EnumChatFormatting.GOLD + "DND";
                        break;
                    case INVISIBLE:
                        status = EnumChatFormatting.GRAY + "Invisible";
                        break;
                    case UNKNOWN:
                    default:
                        status = EnumChatFormatting.DARK_RED + "(UNKNOWN)";
                        break;
                }
                status += " " + EnumChatFormatting.YELLOW + ((u.isBot()) ? "BOT" : "USER");
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + u.getName() + EnumChatFormatting.WHITE + " - " + EnumChatFormatting.DARK_PURPLE + u.getAsTag() + " " + status));
            }
            player.addChatMessage(DiscordChatMod.newLine);
        }
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
