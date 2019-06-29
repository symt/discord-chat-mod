package io.github.symt;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class DAlias extends CommandBase {

    static void setup() {
        try {
            File alias_file = new File(DiscordChatMod.ALIAS_PATH);
            if (alias_file.isFile()) {
                Map<String, String> aliasesFileMap = Files.lines(FileSystems.getDefault().getPath(DiscordChatMod.ALIAS_PATH))
                        .filter(s -> s.matches("^\\w+:\\w+"))
                        .collect(Collectors.toMap(k -> k.split(":")[0], v -> v.split(":")[1]));
                DiscordChatMod.aliases.putAll(aliasesFileMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromFile(String key) {
        try {
            File alias_file = new File(DiscordChatMod.ALIAS_PATH);
            FileWriter fw = new FileWriter(alias_file, false);
            fw.write(removeContentsFromMultilineString(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String removeContentsFromMultilineString(String key) {
        return DiscordChatMod.readFile(DiscordChatMod.ALIAS_PATH, Charset.defaultCharset()).replaceAll("(?m)^" + key + ".*", "");
    }

    private void addToFile(String key, String value) {
        try {
            File alias_file = new File(DiscordChatMod.ALIAS_PATH);
            FileWriter fw = new FileWriter(alias_file, false);
            if (!new File(DiscordChatMod.ALIAS_PATH).isFile()) {
                fw.write(key + ":" + value);
            } else {
                fw.write(removeContentsFromMultilineString(key) + System.getProperty("line.separator") + key + ":" + value);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCommandName() {
        return "dalias";
    }

    public void processCommand(final ICommandSender ics, String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) ics;
            player.addChatMessage(DiscordChatMod.newLine);
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    DiscordChatMod.aliases.put(args[1], args[2]);
                    player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "Alias " + EnumChatFormatting.DARK_GREEN + args[1] + EnumChatFormatting.GREEN + " linked to ID " + EnumChatFormatting.DARK_GREEN + args[2]));
                    addToFile(args[1], args[2]);
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                Iterator it = DiscordChatMod.aliases.entrySet().iterator();
                if (it.hasNext()) {
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.DARK_GREEN + pair.getKey().toString() + EnumChatFormatting.GREEN + " -> " + EnumChatFormatting.DARK_GREEN + pair.getValue(), new Object[0]));
                        it.remove();
                    }
                } else {
                    player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + " Nobody to list", new Object[0]));
                }

            } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                DiscordChatMod.aliases.remove(args[1]);
                removeFromFile(args[1]);
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "SUCCESS!", new Object[0]));
            } else {
                player.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + " Please follow the correct format: /dalias (add|remove|list) (alias) (user id)"));
            }
            player.addChatMessage(DiscordChatMod.newLine);
        }
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dalias (add|remove|list) (alias) (user id)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
