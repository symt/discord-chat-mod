package io.github.symt.listeners;

import io.github.symt.DiscordChatMod;
import io.github.symt.commands.DMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.json.JSONObject;

public class EventListener {

  private static final String USER_AGENT = "Mozilla/5.0";
  private static boolean firstJoin = true;

  @SubscribeEvent
  public void onPlayerJoinEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
    if (firstJoin) {
      firstJoin = false;
      Minecraft.getMinecraft().addScheduledTask(() ->
        new Thread(() -> {
          try {
            URL url = new URL("https://api.github.com/repos/symt/discord-chat-mod/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
              BufferedReader in = new BufferedReader(
                  new InputStreamReader(con.getInputStream()));
              String inputLine;
              StringBuilder response = new StringBuilder();

              while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
              }
              in.close();

              JSONObject json = new JSONObject(response.toString());
              String latest = ((String) json.get("tag_name"));
              String[] latestTag = latest.split("\\.");
              String current = DiscordChatMod.VERSION;
              String[] currentTag = current.split("\\.");

              if (latestTag.length == 3 && currentTag.length == 3) {
                for (int i = 0; i < latestTag.length; i++) {
                  if (latestTag[i].compareTo(currentTag[i]) != 0) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(DiscordChatMod.newLine);
                    if (latestTag[i].compareTo(currentTag[i]) <= -1) {
                      Minecraft.getMinecraft().thePlayer
                          .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                              "You are currently on a pre-release build of DiscordChatMod. Please report any bugs that you may come across"));
                    } else if (latestTag[i].compareTo(currentTag[i]) >= 1) {
                      Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                          EnumChatFormatting.GREEN + "You are currently on version "
                              + EnumChatFormatting.DARK_GREEN + current + EnumChatFormatting.GREEN
                              + " and the latest version is " + EnumChatFormatting.DARK_GREEN
                              + latest + EnumChatFormatting.GREEN
                              + ". Please update to the latest version of DiscordChatMod."));
                    }
                    Minecraft.getMinecraft().thePlayer.addChatMessage(DiscordChatMod.newLine);
                    break;
                  }
                }
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }).start());
    }
  }

  @SubscribeEvent
  public void onPlayerLeaveEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    firstJoin = true;
  }

  @SubscribeEvent
  public void onCommand(CommandEvent event) {
    if (DiscordChatMod.overrideCommand) {
      String commandPrefix;

      switch (event.command.getCommandName().toLowerCase()) {
        case "msg":
          commandPrefix = "dmsg";
          break;
        case "r":
          commandPrefix = "dr";
          break;
        default:
          commandPrefix = null;
      }
      if (event.sender instanceof EntityPlayer && commandPrefix != null) {
        ClientCommandHandler.instance.executeCommand(event.sender,
            String.format("/%1$s %2$s", commandPrefix, DMessage.formatArgs(event.parameters)));
        event.setCanceled(true);
      }
    }
  }
}
