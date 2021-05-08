package me.petterim1.scoreboards;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;

public class APIDownloader {

    static void checkAndRun(Plugin plugin) {
        Server server = plugin.getServer();

        if (server.getPluginManager().getPlugin("ScoreboardPlugin") == null) {
            plugin.getLogger().info("Downloading ScoreboardAPI...");

            String scoreboardApi = server.getFilePath() + "/plugins/ScoreboardAPI.jar";

            try {
                FileOutputStream fos = new FileOutputStream(scoreboardApi);
                fos.getChannel().transferFrom(Channels.newChannel(new URL("https://dl.dropboxusercontent.com/s/hsrflmdxqqrvc0v/ScoreboardAPI.jar").openStream()), 0, Long.MAX_VALUE);
                fos.close();
            } catch (Exception e) {
                plugin.getLogger().info("Failed to download ScoreboardAPI!");
                server.getLogger().logException(e);
                server.getPluginManager().disablePlugin(plugin);
                return;
            }

            plugin.getLogger().info("ScoreboardAPI downloaded successfully");
            server.getPluginManager().loadPlugin(scoreboardApi);
        }

        if (server.getPluginManager().getPlugin("KotlinLib") == null) {
            plugin.getLogger().info("Downloading KotlinLib...");

            String placeholderApi = server.getFilePath() + "/plugins/KotlinLib.jar";

            try {
                FileOutputStream fos = new FileOutputStream(placeholderApi);
                fos.getChannel().transferFrom(Channels.newChannel(new URL("https://dl.dropboxusercontent.com/s/6rmogms1458p369/KotlinLib.jar").openStream()), 0, Long.MAX_VALUE);
                fos.close();
            } catch (Exception e) {
                plugin.getLogger().info("Failed to download KotlinLib!");
                server.getLogger().logException(e);
                server.getPluginManager().disablePlugin(plugin);
                return;
            }

            plugin.getLogger().info("PlaceholderAPI downloaded successfully");
            server.getPluginManager().loadPlugin(placeholderApi);
        }

        if (server.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().info("Downloading PlaceholderAPI...");

            String placeholderApi = server.getFilePath() + "/plugins/PlaceholderAPI.jar";

            try {
                FileOutputStream fos = new FileOutputStream(placeholderApi);
                fos.getChannel().transferFrom(Channels.newChannel(new URL("https://dl.dropboxusercontent.com/s/b5qvtaugosf54am/PlaceholderAPI.jar").openStream()), 0, Long.MAX_VALUE);
                fos.close();
            } catch (Exception e) {
                plugin.getLogger().info("Failed to download PlaceholderAPI!");
                server.getLogger().logException(e);
                server.getPluginManager().disablePlugin(plugin);
                return;
            }

            plugin.getLogger().info("PlaceholderAPI downloaded successfully");
            server.getPluginManager().loadPlugin(placeholderApi);
        }
    }
}