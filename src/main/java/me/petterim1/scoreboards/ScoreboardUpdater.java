package me.petterim1.scoreboards;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;

import cn.nukkit.Server;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;

public class ScoreboardUpdater implements Runnable {

    private Field f1;

    public ScoreboardUpdater(Main main) {
        if (Main.incompatibleJava) {
            main.getLogger().warning("Improved scoreboard updater is not supported on this Java version. Please avoid fast scoreboard updates. For best compatibility and performance, use Java 8 or 11.");
            return;
        }
        try {
            f1 = Scoreboard.class.getDeclaredField("scoreboardLines");
            f1.setAccessible(true);
            Field f1m = Field.class.getDeclaredField("modifiers");
            f1m.setAccessible(true);
            f1m.setInt(f1, f1.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            Map<UUID, Player> players = Server.getInstance().getOnlinePlayers();
            if (!players.isEmpty()) {
                for (Player p : players.values()) {
                    if (!p.spawned || Main.noScoreboardWorlds.contains(p.getLevel().getName())) {
                        Scoreboard previous = Main.scoreboards.remove(p);
                        if (previous != null) {
                            previous.hideFor(p);
                        }
                        continue;
                    }

                    Scoreboard previous = Main.scoreboards.get(p);
                    Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                    ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", Main.scoreboardTitle);

                    int line = 0;
                    boolean needsUpdate = false;
                    for (String text : Main.scoreboardText) {
                        String scoreboardLine = Main.getScoreboardString(p, text);
                        if (previous == null || Main.incompatibleJava) {
                            needsUpdate = true;
                        } else {
                            Object lineObj = ((Long2ObjectMap) f1.get(previous)).get(line);
                            if (lineObj == null) {
                                continue;
                            }

                            Field f2 = lineObj.getClass().getDeclaredField("fakeName");
                            f2.setAccessible(true);

                            Field f2m = Field.class.getDeclaredField("modifiers");
                            f2m.setAccessible(true);
                            f2m.setInt(f2, f2.getModifiers() & ~Modifier.FINAL);

                            String previousLine = (String) f2.get(lineObj);
                            if (!scoreboardLine.equals(previousLine)) {
                                needsUpdate = true;
                            }
                        }
                        scoreboardDisplay.addLine(scoreboardLine, line++);
                    }

                    if (!needsUpdate) {
                        continue;
                    }

                    if (previous != null) {
                        previous.hideFor(p);
                    }

                    scoreboard.showFor(p);
                    Main.scoreboards.put(p, scoreboard);
                }
            }
        } catch (Exception e) {
            if (Nukkit.DEBUG > 1) {
                e.printStackTrace();
            }
        }
    }
}
