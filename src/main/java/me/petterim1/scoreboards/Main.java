package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.HashMap;
import java.util.Map;

public class Main extends PluginBase implements Listener {

    private static final int currentConfig = 2;

    static Config config;

    private int line = 0;

    static Map<Player, Scoreboard> scoreboards = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        if (config.getInt("version") < currentConfig) {
            getServer().getLogger().warning("The config file of SimpleScoreboards plugin is outdated. Please delete the old config.yml file.");
        }

        APIDownloader.checkAndRun(this);

        getServer().getPluginManager().registerEvents(this, this);

        if (config.getInt("update") > 0) {
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, new ScoreboardUpdater(this), config.getInt("update"), config.getInt("update"), config.getBoolean("async", true));
        }

        try {
            Class.forName("kdr.Main");
        } catch (Exception e) {
            config.getStringList("text").forEach((text) -> {
                if (text.contains("%kdr_")) {
                    getLogger().warning("Scoreboard has KDR placeholders but KDR plugin not found. This may lead to errors.");
                }
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Scoreboard scoreboard  = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", config.getString("title"));

        config.getStringList("text").forEach((text) -> {
            scoreboardDisplay.addLine(PlaceholderAPI.getInstance().translateString(text
                            .replace("%economy_money%", getMoney(p))
                            .replace("%factions_name%", getFaction(p))
                            .replace("%kdr_kdr%", String.valueOf(kdr.Main.plugin.getKDR(p)))
                            .replace("%kdr_kills%", String.valueOf(kdr.Main.plugin.getKills(p)))
                            .replace("%kdr_deaths%", String.valueOf(kdr.Main.plugin.getDeaths(p)))
                            .replace("%kdr_topkdr%", String.valueOf(kdr.Main.plugin.getTopKDRScore()))
                            .replace("%kdr_topkdrplayer%", kdr.Main.plugin.getTopKDRPlayer())
                            .replace("%kdr_topkills%", String.valueOf(kdr.Main.plugin.getTopKills()))
                            .replace("%kdr_topdeaths%", String.valueOf(kdr.Main.plugin.getTopDeaths()))
                            .replace("%kdr_topkillsplayer%", kdr.Main.plugin.getTopKillsPlayer())
                            .replace("%kdr_topdeathsplayer%", kdr.Main.plugin.getTopDeathsPlayer())
                    , p), line++);
        });

        scoreboard.showFor(p);
        scoreboards.put(p, scoreboard);
        line = 0;
    }

    static String getMoney(Player p) {
        try {
            Class.forName("me.onebone.economyapi.EconomyAPI");
            return Double.toString(me.onebone.economyapi.EconomyAPI.getInstance().myMoney(p));
        } catch (Exception ex) {
            return "EconomyAPI not found";
        }
    }

    static String getFaction(Player p) {
        try {
            Class.forName("com.massivecraft.factions.P");
            return com.massivecraft.factions.P.p.getPlayerFactionTag(p);
        } catch (Exception e) {
            return "Factions not found";
        }
    }
}
