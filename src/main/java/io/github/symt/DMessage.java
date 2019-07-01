package io.github.symt;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DMessage extends CommandBase {
    static void sendMessageToBot(String content, String prefix, String user, EntityPlayer player) {
        if (content.contains("~=~")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Hold up there, you aren't allowed to use \"~=~\" because it is important!!"));
        } else {
            Guild guild = DiscordChatMod.jda.getGuildById("594325352944238623");
            guild.getTextChannelById("594358847435702282").sendMessage(prefix + "~=~" + content).queue();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "To " + EnumChatFormatting.DARK_PURPLE + user + EnumChatFormatting.LIGHT_PURPLE + ": " + EnumChatFormatting.RESET + content));
        }
    }

    static void sendMessageToUser(String content, User user, EntityPlayer player) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(content).queue());
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "To " + EnumChatFormatting.DARK_PURPLE + user.getName() + EnumChatFormatting.LIGHT_PURPLE + ": " + EnumChatFormatting.RESET + content));
    }

    public String getCommandName() {
        return "dmsg";
    }

    public void processCommand(final ICommandSender ics, String[] args) {
        if (ics instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) ics;
            if (args.length >= 2) {
                final String ID = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                JDA jda = DiscordChatMod.jda;

                List<Member> membersWithName = new ArrayList<>();
                List<User> usersWithName = new ArrayList<>();
                if (!ID.contains("#")) {
                    jda.getGuilds().forEach(guild -> membersWithName.addAll(guild.getMembersByName(ID, true)));
                    membersWithName.forEach(member -> usersWithName.add(member.getUser()));
                } else {
                    jda.getUsers().stream().filter(user -> user.getAsTag().equalsIgnoreCase(ID)).findFirst().ifPresent(usersWithName::add);
                }

                if (usersWithName.size() > 1) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Multiple users with that name, please add a tag."));
                } else {
                    if (usersWithName.isEmpty()) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "No user found with that name"));
                    } else {
                        User reqUser = usersWithName.get(0);

                        final String content = formatArgs(args);

                        if (reqUser.isBot()) {
                            Guild mainGuild = jda.getGuildById("594325352944238623");
                            IChatComponent link = new ChatComponentText("github repo");
                            link.getChatStyle().setUnderlined(true);
                            link.getChatStyle().setBold(true);
                            link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/symt/discord-chat-mod/blob/master/README.md"));
                            if (mainGuild != null) {
                                if (reqUser.getJDA().getGuilds().contains(mainGuild)) {
                                    sendMessageToBot(content, reqUser.getAsTag(), reqUser.getName(), player);
                                } else {
                                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "The bot that you are requesting is not in the discord bot connector server. Open an issue at the ").appendSibling(link).appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " and request for access")));
                                }
                            } else {
                                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You are not in the discort bot connector server. Open an issue at the ").appendSibling(link).appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " and request for access")));
                            }
                        } else {
                            sendMessageToUser(content, reqUser, player);
                        }
                    }
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Please provide a valid user and message"));
            }
        }
    }

    static String formatArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(" ");
            builder.append(s);
        }
        return builder.toString().substring(1);
    }

    public String getCommandUsage(final ICommandSender sender) {
        return "/dmsg (user) (message)";
    }

    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
