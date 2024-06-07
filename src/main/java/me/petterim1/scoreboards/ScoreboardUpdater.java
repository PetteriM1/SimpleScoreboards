package me.petterim1.scoreboards;

import cn.nukkit.Player;

import cn.nukkit.Server;
import cn.nukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUpdater implements Runnable {

    @Override
    public void run() {
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (!p.spawned || Main.noScoreboardWorlds.contains(p.getLevel().getName())) {
                continue;
            }

            Scoreboard scoreboard = Main.scoreboards.get(p);
            if (scoreboard != null) {
                scoreboard.holdUpdates();

                boolean needsUpdate = false;

                List<String> translatedStrings = new ArrayList<>();

                for (String line : Main.scoreboardText) {
                    String translated = Main.getScoreboardString(p, line);
                    translatedStrings.add(translated);

                    if (!scoreboard.getScores().containsKey(translated)) {
                        needsUpdate = true;
                    }
                }

                if (needsUpdate) {
                    scoreboard.clear();

                    int line = 0;
                    for (String text : translatedStrings) {
                        scoreboard.setScore(text, line++);
                    }
                }

                scoreboard.unholdUpdates();
            }
        }
    }
}
