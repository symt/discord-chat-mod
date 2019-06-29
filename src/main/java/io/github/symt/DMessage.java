package io.github.symt;

import com.sun.media.jfxmedia.logging.Logger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Level;

import java.util.Arrays;

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
                User user;
                if (DiscordChatMod.aliases.containsKey(ID)) {
                    user = jda.getUserById(DiscordChatMod.aliases.get(ID));
                } else {
                    user = jda.getUserById(Long.parseLong(ID));
                }
                user.openPrivateChannel().queue((channel) -> channel.sendMessage(content).queue());
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.LIGHT_PURPLE + "To " + user.getName() + ": " + EnumChatFormatting.RESET + content));
            } else {
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Please provide a user id (or an alias) and message", new Object[0]));
            }
        }
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dmsg (user id | alias) (message)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
