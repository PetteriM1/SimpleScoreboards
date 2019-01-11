package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.HashMap;
import java.util.Map;

public class Main extends PluginBase implements Listener {

    private static final int currentConfig = 1;

    public static Config config;

    private int line = 0;

    public static Map<Player, Scoreboard> scoreboards = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        if (config.getInt("version") < currentConfig) {
            getServer().getLogger().warning("The config file of SimpleScoreboards plugin is outdated. Please delete the old config.yml file.");
        }

        try {
            Class.forName("de.theamychan.scoreboard.ScoreboardPlugin");
        } catch (Exception e) {
            getServer().getLogger().warning("ScoreboardAPI not found, download it from https://nukkitx.com/resources/scoreboardapi.181/");
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(this, this);

        if (config.getInt("update") > 0) {
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, new ScoreboardUpdater(this), config.getInt("update"), config.getInt("update"), true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Scoreboard scoreboard  = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", config.getString("title"));

        config.getStringList("text").forEach((text) -> {
            scoreboardDisplay.addLine(text.replaceAll("§", "\u00A7"), line++);
        });

        scoreboard.showFor(p);
        scoreboards.put(p, scoreboard);
        line = 0;
    }
}

class ScoreboardUpdater extends Thread {

    private Main plugin;

    private int line = 0;

    public ScoreboardUpdater(Main plugin) {
        this.plugin = plugin;
        setName("ScoreboardUpdater");
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers().values()) {
            try {
                Main.scoreboards.get(p).hideFor(p);
            } catch (Exception e) {}

            Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
            ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", Main.config.getString("title"));

            Main.config.getStringList("text").forEach((text) -> {
                String money = "null";

                try {
                    Class.forName("me.onebone.economyapi.EconomyAPI");
                    money = Double.toString(me.onebone.economyapi.EconomyAPI.getInstance().myMoney(p));
                } catch (Exception e) {}

                scoreboardDisplay.addLine(text
                        .replaceAll("§", "\u00A7")
                        .replaceAll("<NAME>", p.getName())
                        .replaceAll("<WORLD>", p.getLevel().getName())
                        .replaceAll("<X>", Integer.toString((int) p.x))
                        .replaceAll("<Y>", Integer.toString((int) p.y))
                        .replaceAll("<Z>", Integer.toString((int) p.z))
                        .replaceAll("<PLAYERS>", Integer.toString(plugin.getServer().getOnlinePlayers().size()))
                        .replaceAll("<MAXPLAYERS>", Integer.toString(plugin.getServer().getMaxPlayers()))
                        .replaceAll("<PING>", Integer.toString(p.getPing()))
                        .replaceAll("<MONEY>", money), line++);
            });

            scoreboard.showFor(p);
            Main.scoreboards.put(p, scoreboard);
            line = 0;
        }
    }
}
