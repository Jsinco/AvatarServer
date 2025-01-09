package dev.jsinco.avatarserver;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.avatarserver.server.GenericServer;
import dev.jsinco.avatarserver.server.Handler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public final class AvatarServer extends JavaPlugin {

    private GenericServer genericServer;

    @Override
    public void onEnable() {
        FileLibSettings.set(getDataFolder());
        getLogger().log(Level.INFO, "Starting Jetty");
        genericServer = new GenericServer(Handler.Environment.PAPER);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            genericServer.start();
        });
    }


    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Stopping Jetty");
        try {
            genericServer.stop();
            getServer().getScheduler().cancelTasks(this); // Just cancel jetty this way
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static URL getTextureFromUUIDOrName(String uuidOrName) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(uuidOrName);
        } catch (IllegalArgumentException e) {
            // Not a UUID
        }

        Player player;
        if (uuid == null) {
            player = Bukkit.getPlayer(uuidOrName);
        } else {
            player = Bukkit.getPlayer(uuid);
        }


        if (player == null) {
            return null;
        }
        return player.getPlayerProfile().getTextures().getSkin();
    }
}