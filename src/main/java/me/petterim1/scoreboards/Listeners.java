package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.scoreboard.Scoreboard;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerLocallyInitializedEvent e) {
        Player p = e.getPlayer();

        if (!Main.noScoreboardWorlds.contains(p.getLevel().getName())) {
            createScoreboard(p);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && !e.getOrigin().equals(e.getTarget())) {
            Player p = (Player) e.getEntity();

            if (Main.noScoreboardWorlds.contains(e.getTarget().getName())) {
                destroyScoreboard(p);
            } else if (Main.noScoreboardWorlds.contains(e.getOrigin().getName())) {
                createScoreboard(p);
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        destroyScoreboard(e.getPlayer());
    }

    private static void createScoreboard(Player p) {
        Scoreboard scoreboard = new Scoreboard(Main.scoreboardTitle, Scoreboard.SortOrder.ASCENDING, Scoreboard.DisplaySlot.SIDEBAR);
        int line = 0;
        for (String text : Main.scoreboardText) {
            scoreboard.setScore(Main.getScoreboardString(p, text), line++);
        }

        Main.scoreboards.put(p, scoreboard);
        scoreboard.showTo(p);
    }

    private static void destroyScoreboard(Player p) {
        Scoreboard scoreboard = Main.scoreboards.remove(p);
        if (scoreboard != null) {
            scoreboard.hideFor(p);
        }
    }
}
