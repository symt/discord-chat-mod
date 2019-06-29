package io.github.symt;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DToken extends CommandBase {
    public String getCommandName() {
        return "dtoken";
    }

    public void processCommand(final ICommandSender ics, final String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)ics;
            player.addChatMessage(DiscordChatMod.newLine);
            if (args.length == 1) {
                File token_file = new File(DiscordChatMod.TOKEN_PATH);
                if (token_file.isFile()) {
                    player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Your token has been replaced", new Object[0]));
                } else {
                    player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Your token has been set", new Object[0]));
                }
                try {
                    FileWriter fw = new FileWriter(token_file, false);
                    fw.write(args[0]);
                    fw.close();
                    DiscordChatMod.startup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Please provide a token - /dtoken (token)", new Object[0]));
            }
            player.addChatMessage(DiscordChatMod.newLine);
        }
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dtoken (discord bot token)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
