package io.github.symt;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DR extends CommandBase {
    public String getCommandName() {
        return "dr";
    }

    public void processCommand(final ICommandSender ics, String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) ics;
            if (DiscordChatMod.lastUser != null) {
                if (DiscordChatMod.lastUser.isBot()) {
                    DMessage.sendMessageToBot(DMessage.formatArgs(args), DiscordChatMod.lastUser.getAsTag(), DiscordChatMod.lastUser.getName(), player);
                } else {
                    DMessage.sendMessageToUser(DMessage.formatArgs(args), DiscordChatMod.lastUser, player);
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "No one has sent you a message."));
            }
        }
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dr (message)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
