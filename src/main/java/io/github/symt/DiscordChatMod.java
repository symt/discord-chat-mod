package io.github.symt;

import io.github.symt.commands.DList;
import io.github.symt.commands.DMessage;
import io.github.symt.commands.DR;
import io.github.symt.commands.DToken;
import io.github.symt.listeners.EventListener;
import io.github.symt.listeners.MessageListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = DiscordChatMod.MODID, version = DiscordChatMod.VERSION)
public class DiscordChatMod {

  public static final String VERSION = "1.4.0";
  public static final IChatComponent newLine = new ChatComponentTranslation("", new Object[0]);
  public static final String TOKEN_PATH = "token.txt";
  static final String MODID = "DiscordChatMod";
  public static JDA jda;
  public static User lastUser;

  public static Logger logger;

  public static void startup() {
    try {
      jda = new JDABuilder(new BufferedReader(new FileReader(TOKEN_PATH)).readLine())
          .addEventListener(new MessageListener())
          .build();
      jda.awaitReady();
    } catch (InterruptedException | LoginException | IOException e) {
      e.printStackTrace();
    }
  }

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    MinecraftForge.EVENT_BUS.register(new EventListener());
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    ClientCommandHandler.instance.registerCommand(new DToken());
    ClientCommandHandler.instance.registerCommand(new DMessage());
    ClientCommandHandler.instance.registerCommand(new DR());
    ClientCommandHandler.instance.registerCommand(new DList());
    if (new File(DiscordChatMod.TOKEN_PATH).isFile()) {
      startup();
    }
  }
}
