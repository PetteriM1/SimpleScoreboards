package me.petterim1.scoreboards;

import cn.nukkit.Player;

import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

public class ScoreboardUpdater extends Thread {

    private Main plugin;

    private int line = 0;

    ScoreboardUpdater(Main plugin) {
        this.plugin = plugin;
        setName("ScoreboardUpdater");
    }

    @Override
    public void run() {
        try {
            for (Player p : plugin.getServer().getOnlinePlayers().values()) {
                try {
                    Main.scoreboards.get(p).hideFor(p);
                } catch (Exception ignored) {}

                Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", Main.config.getString("title"));

                Main.config.getStringList("text").forEach((text) -> {
                    scoreboardDisplay.addLine(PlaceholderAPI.getInstance().translateString(text
                                    .replace("%economy_money%", Main.getMoney(p))
                                    .replace("%factions_name%", Main.getFaction(p))
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
                Main.scoreboards.put(p, scoreboard);
                line = 0;
            }
        } catch (Exception ignored) {}
    }
}