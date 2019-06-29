package io.github.symt;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mod(modid = DiscordChatMod.MODID, version = DiscordChatMod.VERSION)
public class DiscordChatMod {
    static final String MODID = "DiscordChatMod";
    static final String VERSION = "1.0";
    static final IChatComponent newLine = new ChatComponentTranslation("", new Object[0]);
    static final String TOKEN_PATH = "token.txt";
    static JDA jda;

    static Logger logger;

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

    static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DToken());
        ClientCommandHandler.instance.registerCommand(new DMessage());
        if (new File(DiscordChatMod.TOKEN_PATH).isFile()) {
            startup();
        }
    }
}
