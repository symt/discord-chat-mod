package io.github.symt.commands;

import io.github.symt.DiscordChatMod;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class DList extends CommandBase {

  private static List<IChatComponent> statuses = null;
  private static long lastUpdate = 0;

  @Override
  public String getCommandName() {
    return "dlist";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/dlist [page]";
  }

  @Override
  public void processCommand(ICommandSender ics, String[] args) {
    if (ics instanceof EntityPlayer) {
      final EntityPlayer player = (EntityPlayer) ics;
      if (System.currentTimeMillis() - lastUpdate >= 60000) {
        updateStatuses();
        lastUpdate = System.currentTimeMillis();
      }

      int maxPage = statuses.size() / 10 + 1;
      int page;
      if (args.length == 1 && args[0] != null && isString(args[0])) {
        page = Integer.parseInt(args[0]);
      } else {
        page = 1;
      }
      page = (maxPage < page) ? maxPage : (page <= 0) ? 1 : page;

      ChatComponentText left = new ChatComponentText("<-");
      left.getChatStyle()
          .setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/dlist " + (page - 1)));
      ChatComponentText right = new ChatComponentText("->");
      right.getChatStyle()
          .setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/dlist " + (page + 1)));
      IChatComponent header = left.appendSibling(
          new ChatComponentText(EnumChatFormatting.YELLOW + " Page " + page + "/" + maxPage + " "))
          .appendSibling(right);
      player.addChatMessage(DiscordChatMod.newLine);
      player.addChatMessage(header);
      displayPage(player, page);
      player.addChatMessage(new ChatComponentText(
          EnumChatFormatting.YELLOW + new String(new char[("<- Page " + page + "/" + maxPage + " ->").length()])
              .replace("\0", "-")));
      player.addChatMessage(DiscordChatMod.newLine);
    }
  }

  private void displayPage(EntityPlayer player, int page) {
    for (int i = (page-1)*10; i < (page-1)*10+10 && i < statuses.size(); i++) {
      player.addChatMessage(statuses.get(i));
    }
  }

  private boolean isString(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private void updateStatuses() {
    statuses = new ArrayList<>();
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
    for (Member m : allMembers) {
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
      status += " " + EnumChatFormatting.YELLOW + ((m.getUser().isBot()) ? "BOT" : "USER");
      status = EnumChatFormatting.DARK_PURPLE + m.getUser().getName() + EnumChatFormatting.WHITE
          + " - " + EnumChatFormatting.DARK_PURPLE + m.getUser().getAsTag() + " " + status;
      statuses.add(new ChatComponentText(status));
    }
  }

  public boolean canCommandSenderUseCommand(final ICommandSender sender) {
    return true;
  }
}
