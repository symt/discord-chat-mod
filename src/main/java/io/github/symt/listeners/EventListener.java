package io.github.symt.listeners;

import io.github.symt.DiscordChatMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventListener {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static boolean firstJoin = true;

    @SubscribeEvent
    public static void onPlayerJoinEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (firstJoin) {
            firstJoin = false;
            Minecraft.getMinecraft().addScheduledTask(() -> {
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

                        JSONObject json = new JSONObject(response);
                        String latest = ((String) json.get("tag_name"));
                        String[] latestTag = latest.split(".");
                        String current = DiscordChatMod.VERSION;
                        String[] currentTag = current.split(".");

                        if (latestTag.length == 3 && currentTag.length == 3) {
                            for (int i = 0; i < latestTag.length; i++) {
                                if (latestTag[i].compareTo(currentTag[i]) != 0) {
                                    Minecraft.getMinecraft().thePlayer.addChatMessage(DiscordChatMod.newLine);
                                    if (latestTag[i].compareTo(currentTag[i]) <= -1) {
                                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You are currently on a pre-release build. Please report any bugs that you may come across"));
                                    } else if (latestTag[i].compareTo(currentTag[i]) >= 1) {
                                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You are currently on version " + EnumChatFormatting.DARK_GREEN + current + EnumChatFormatting.GREEN + " and the latest version is " + EnumChatFormatting.DARK_GREEN + latest + ". Please update to the latest version."));
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
            });
        }
    }

    @SubscribeEvent
    public void onPlayerLeaveEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        firstJoin = true;
    }
}
