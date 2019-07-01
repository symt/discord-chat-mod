package io.github.symt;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.LinkedList;

public class MessageListener extends ListenerAdapter {
    LinkedList<String> queue = new LinkedList<>();
    int maxSize = 10;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();
        String msg = event.getMessage().getContentDisplay();
        if (!DiscordChatMod.jda.getSelfUser().equals(author)) {
            if (channel.getType() == ChannelType.TEXT) {
                String[] messageContents = msg.split("~=~");
                if (messageContents[0].equals(DiscordChatMod.jda.getSelfUser().getAsTag()) && channel.getName().equalsIgnoreCase("bot-communications")) {
                    if (queue.isEmpty() || !queue.getLast().equals(msg)) {
                        DiscordChatMod.lastUser = author;
                        queue.add(msg);
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "From " + EnumChatFormatting.DARK_PURPLE + author.getName() + EnumChatFormatting.LIGHT_PURPLE + ": " + EnumChatFormatting.RESET + messageContents[1]));
                    }
                }
            } else if (channel.getType() == ChannelType.PRIVATE) {
                DiscordChatMod.lastUser = author;
                queue.add(msg);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "From " + EnumChatFormatting.DARK_PURPLE + author.getName() + EnumChatFormatting.LIGHT_PURPLE + ": " + EnumChatFormatting.RESET + msg));
            }
        }

        if (queue.size() >= maxSize) {
            queue.removeFirst();
        }
    }
}
