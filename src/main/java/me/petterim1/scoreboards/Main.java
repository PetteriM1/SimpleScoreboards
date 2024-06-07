package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.utils.Config;

import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends PluginBase implements Listener {

    private static final int currentConfig = 3;

    public static String scoreboardTitle;
    public static final List<String> scoreboardText = new ArrayList<>();
    public static final Set<String> noScoreboardWorlds = new HashSet<>();

    static final Map<Player, Scoreboard> scoreboards = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        if (!APIDownloader.checkAndRun(this)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        Config config = getConfig();

        if (config.getInt("version") < currentConfig) {
            getLogger().warning("The config file of SimpleScoreboards plugin is outdated. Please delete the old config.yml file.");
        }

        scoreboardTitle = config.getString("title");
        scoreboardText.addAll(config.getStringList("text"));
        noScoreboardWorlds.addAll(config.getStringList("noScoreboardWorlds"));

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        if (config.getInt("update") > 0) {
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, new ScoreboardUpdater(), config.getInt("update"), config.getInt("update"), config.getBoolean("async", true));
        } else {
            getLogger().notice("Scoreboard updating is not enabled (update <= 0)");
        }
    }

    static String getScoreboardString(Player p, String text) {
        try {
            return PlaceholderAPI.getInstance().translateString(getKDRStatsReplaced(p, text), p);
        } catch (Exception e) {
            e.printStackTrace();
            return "PlaceholderAPI error!";
        }
    }

    private static String getKDRStatsReplaced(Player p, String textToReplace) {
        try {
            Class.forName("kdr.Main");

            return textToReplace.replace("%kdr_kdr%", String.format("%.2f", kdr.Main.plugin.getKDR(p)))
                    .replace("%kdr_kills%", String.valueOf(kdr.Main.plugin.getKills(p)))
                    .replace("%kdr_deaths%", String.valueOf(kdr.Main.plugin.getDeaths(p)))
                    .replace("%kdr_topkdr%", String.format("%.2f", kdr.Main.plugin.getTopKDRScore()))
                    .replace("%kdr_topkdrplayer%", kdr.Main.plugin.getTopKDRPlayer())
                    .replace("%kdr_topkills%", String.valueOf(kdr.Main.plugin.getTopKills()))
                    .replace("%kdr_topdeaths%", String.valueOf(kdr.Main.plugin.getTopDeaths()))
                    .replace("%kdr_topkillsplayer%", kdr.Main.plugin.getTopKillsPlayer())
                    .replace("%kdr_topdeathsplayer%", kdr.Main.plugin.getTopDeathsPlayer());
        } catch (Exception e) {
            return textToReplace;
        }
    }
}
