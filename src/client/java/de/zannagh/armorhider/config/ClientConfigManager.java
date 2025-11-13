package de.zannagh.armorhider.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zannagh.armorhider.ArmorHider;
import de.zannagh.armorhider.resources.PlayerConfig;
import de.zannagh.armorhider.netPackets.SettingsC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ClientConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = new File("config", "armor-hider.json").toPath();

    private static PlayerConfig CURRENT = PlayerConfig.defaults(UUID.randomUUID(), "dummy");
    
    private static Map<String, PlayerConfig> SERVERCONFIG = new HashMap<>();

    public static void updateName(String name){
        CURRENT.playerName = name;
        save();
    }

    public static void updateId(UUID id){
        CURRENT.playerId = id;
        save();
    }
    
    public static void setHelmetTransparency(double transparency){
        CURRENT.helmetTransparency = transparency;
        save();
    }

    public static void setChestTransparency(double transparency){
        CURRENT.chestTransparency = transparency;
        save();
    }

    public static void setLegsTransparency(double transparency){
        CURRENT.legsTransparency = transparency;
        save();
    }

    public static void setBootsTransparency(double transparency){
        CURRENT.bootsTransparency = transparency;
        save();
    }
    
    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE)) {
                    PlayerConfig c = GSON.fromJson(r, PlayerConfig.class);
                    if (c != null) {
                        CURRENT = c;
                        ArmorHider.LOGGER.info("Loaded client config from file.");
                        ArmorHider.LOGGER.info("Current config: {}", GSON.toJson(CURRENT));
                    }
                }
            } else {
                save();
            }
        } catch (IOException e) {
            ArmorHider.LOGGER.error("Failed to load client config!", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(CURRENT, w);
                ArmorHider.LOGGER.info("Saved client config to file.");
                if (MinecraftClient.getInstance().isConnectedToLocalServer() 
                        || MinecraftClient.getInstance().getServer() != null 
                        || (MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().getNetworkHandler().getServerInfo() != null)) {
                    ArmorHider.LOGGER.info("Sending to server...");
                    ClientPlayNetworking.send(new SettingsC2SPacket(get()));
                    ArmorHider.LOGGER.info("Saved client config and sent to server. New config is {}", GSON.toJson(CURRENT));
                }
            }
        } catch (IOException e) {
            ArmorHider.LOGGER.error("Failed to save client config!", e);
        }
    }
    
    public static Map<String, PlayerConfig> getServerConfig(){ return SERVERCONFIG; }
    
    public static void setServerConfig(List<PlayerConfig> serverConfig) {
        ArmorHider.LOGGER.info("Setting server config to {}", GSON.toJson(serverConfig));
        SERVERCONFIG = new HashMap<>();
        serverConfig.forEach(c -> {
            SERVERCONFIG.put(c.playerName, c);
        });
    }

    public static PlayerConfig get() { return CURRENT; }
    public static void set(PlayerConfig cfg) { CURRENT = cfg; save(); }

    public static PlayerConfig getConfigForPlayer(String playerName) {
        if (playerName == null) {
            return CURRENT;
        }
        var config = SERVERCONFIG.getOrDefault(playerName, null);
        if (config != null) {
            return config;
        }
        else {
            if (!SERVERCONFIG.containsKey(playerName) && Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getProfile().name().equals(playerName)) {
                SERVERCONFIG.put(playerName, CURRENT);
                return CURRENT;
            }
            ArmorHider.LOGGER.warn("Failed to get config for player by id, trying by name. {} {}", playerName, playerName);
            var nameBasedConfig = SERVERCONFIG.values().stream().filter(c -> c.playerName.equals(playerName)).findFirst();
            if (nameBasedConfig.isPresent()) {
                ArmorHider.LOGGER.info("Found config for player by name {}, returning config", playerName);
                return nameBasedConfig.get();
            }
        }
        ArmorHider.LOGGER.warn("Failed to get config for player {}", playerName);
        return CURRENT;
    }

}