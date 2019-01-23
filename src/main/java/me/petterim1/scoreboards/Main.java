package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import com.creeperface.nukkit.placeholderapi.PlaceholderAPIIml;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.HashMap;
import java.util.Map;

public class Main extends PluginBase implements Listener {

    private static final int currentConfig = 2;

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

        APIDownloader.checkAndRun(this);

        getServer().getPluginManager().registerEvents(this, this);

        if (config.getInt("update") > 0) {
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, new ScoreboardUpdater(this), config.getInt("update"), config.getInt("update"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Scoreboard scoreboard  = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", config.getString("title"));
        PlaceholderAPI api = PlaceholderAPIIml.getInstance();

        config.getStringList("text").forEach((text) -> {
            scoreboardDisplay.addLine(api.translateString(text.replaceAll("%economy_money%", getMoney(p)).replaceAll("%factions_name%", getFaction(p)), p).replaceAll("§", "\u00A7"), line++);
        });

        scoreboard.showFor(p);
        scoreboards.put(p, scoreboard);
        line = 0;
    }

    public static String getMoney(Player p) {
        try {
            Class.forName("me.onebone.economyapi.EconomyAPI");
            return Double.toString(me.onebone.economyapi.EconomyAPI.getInstance().myMoney(p));
        } catch (Exception ex) {
            return "EconomyAPI not found";
        }
    }

    public static String getFaction(Player p) {
        try {
			Class.forName("com.massivecraft.factions.P");
			return com.massivecraft.factions.P.p.getPlayerFactionTag(p);
		} catch (Exception e) {
			return "Factions not found";
		}
    }
}
