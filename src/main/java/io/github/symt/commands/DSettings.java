package io.github.symt.commands;

import io.github.symt.DiscordChatMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DSettings extends CommandBase {

  private Map<String, String> settings = new HashMap<String, String>() {{
    put("drUser", "Set user for /dr prior to recieving a message from the user");
  }};

  public String getCommandName() {
    return "dsettings";
  }

  public void processCommand(final ICommandSender ics, String[] args) {
    if (ics instanceof EntityPlayer) {
      final EntityPlayer player = (EntityPlayer) ics;
      if (args[1] != null && args[0] != null) {

        switch (args[0].toLowerCase()) {
          case "replyUser":
            List<User> usersWithName = new ArrayList<>();
            List<Member> membersWithName = new ArrayList<>();
            if (args[1].contains("#")) {
              DiscordChatMod.jda.getUsers().stream()
                  .filter(user -> user.getAsTag().equalsIgnoreCase(args[1])).findFirst()
                  .ifPresent(usersWithName::add);
            } else {
              DiscordChatMod.jda.getGuilds()
                  .forEach(guild -> membersWithName.addAll(guild.getMembersByName(args[1], true)));
              membersWithName.forEach(member -> usersWithName.add(member.getUser()));
            }

            if (usersWithName.size() > 1) {
              player.addChatMessage(new ChatComponentText(
                  EnumChatFormatting.GREEN + "Multiple users with that name, please add a tag."));
            } else {
              DiscordChatMod.lastUser = usersWithName.get(0);
              player.addChatMessage(new ChatComponentText(
                  EnumChatFormatting.GREEN + "You can now use /dr for "
                      + EnumChatFormatting.DARK_GREEN + args[1]));
            }
            break;
          case "mode":
            switch (args[1].toLowerCase()) {
              case "receive":
                DiscordChatMod.userMode = DiscordChatMod.Mode.RECEIVE;
                break;
              case "normal":
              default:
                DiscordChatMod.userMode = DiscordChatMod.Mode.NORMAL;
            }
            break;
          case "overrideCommand":
            DiscordChatMod.overrideCommand ^= true;
            break;
          case "setReceiver":

          default:
            listSettings(player);
        }
      } else if (args[0] != null) {
        listSettings(player);
      }
    }
  }

  private void listSettings(EntityPlayer player) {

  }

  public String getCommandUsage(final ICommandSender sender) {
    return "/dsettings (settings) (value)";
  }

  public boolean canCommandSenderUseCommand(final ICommandSender sender) {
    return true;
  }
}
