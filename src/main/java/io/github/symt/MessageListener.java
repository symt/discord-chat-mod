package io.github.symt;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
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
        String[] messageContents = msg.split("|||");
        if (messageContents[0].equals(DiscordChatMod.jda.getSelfUser().getAsTag()) && !DiscordChatMod.jda.getSelfUser().equals(author) && channel.getName().equalsIgnoreCase("bot-communications")) {
            if (queue.isEmpty() || !queue.getLast().equals(msg)) {
                queue.add(msg);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation(EnumChatFormatting.LIGHT_PURPLE + "From " + author.getName() + ": " + EnumChatFormatting.RESET + messageContents[1], new Object[0]));
            }
            if (queue.size() >= maxSize) {
                queue.removeFirst();
            }
        }
    }
}
