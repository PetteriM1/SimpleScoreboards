package me.petterim1.scoreboards;

import cn.nukkit.Player;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.Map;
import java.util.UUID;

public class ScoreboardUpdater extends Thread {

    private Main plugin;

    private int line;

    ScoreboardUpdater(Main plugin) {
        this.plugin = plugin;
        setName("ScoreboardUpdater");
    }

    @Override
    public void run() {
        try {
            Map<UUID, Player> players = plugin.getServer().getOnlinePlayers();
            if (!players.isEmpty()) {
                for (Player p : players.values()) {
                    if (!p.spawned || Main.noScoreboardWorlds.contains(p.getLevel().getName())) {
                        continue;
                    }

                    Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                    ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", Main.config.getString("title"));

                    Main.config.getStringList("text").forEach((text) -> scoreboardDisplay.addLine(Main.getScoreboardString(p, text), line++));

                    try {
                        Main.scoreboards.get(p).hideFor(p);
                    } catch (Exception ignored) {
                    }

                    scoreboard.showFor(p);
                    Main.scoreboards.put(p, scoreboard);
                    line = 0;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
