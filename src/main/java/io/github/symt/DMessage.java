package io.github.symt;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DMessage extends CommandBase {
    public String getCommandName() {
        return "dmsg";
    }

    public void processCommand(final ICommandSender ics, String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) ics;
            if (args.length >= 2) {
                final String ID = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                StringBuilder builder = new StringBuilder();
                for (String s : args) {
                    builder.append(" " + s);
                }
                final String content = builder.toString().substring(1);
                JDA jda = DiscordChatMod.jda;
                Guild guild = jda.getGuildById("594325352944238623");
                List<Member> usersWithName = new ArrayList<>();
                if (!ID.contains("#")) {
                    usersWithName = guild.getMembersByName(ID, true);
                } else {
                    List<Member> allUsers = guild.getMembers();
                    for (Member m : allUsers) {
                        if (m.getUser().getAsTag().equalsIgnoreCase(ID)) {
                            usersWithName.add(m);
                            break;
                        }
                    }
                }

                if (usersWithName.size() > 1) {
                    player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Multiple users with that name, please add a tag.", new Object[0]));
                } else {
                    if (usersWithName.isEmpty()) {
                        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "No user found with that name", new Object[0]));
                    } else {
                        String prefix = usersWithName.get(0).getUser().getAsTag() + "|||";
                        guild.getTextChannelById("594358847435702282").sendMessage(prefix + content).queue();
                        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.LIGHT_PURPLE + "To " + ID + ": " + EnumChatFormatting.RESET + content));
                    }
                }
            } else {
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Please provide a valid user and message", new Object[0]));
            }
        }
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dmsg (user) (message)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
