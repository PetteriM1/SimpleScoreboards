package me.petterim1.scoreboards;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerLocallyInitializedEvent e) {
        Player p = e.getPlayer();
        if (!Main.noScoreboardWorlds.contains(p.getLevel().getName())) {
            Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
            ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "dumy", Main.scoreboardTitle);
            int line = 0;
            for (String text : Main.scoreboardText) {
                scoreboardDisplay.addLine(Main.getScoreboardString(p, text), line++);
            }
            scoreboard.showFor(p);
            Main.scoreboards.put(p, scoreboard);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && !e.getOrigin().equals(e.getTarget())) {
            if (Main.noScoreboardWorlds.contains(e.getTarget().getName())) {
                Player p = (Player) e.getEntity();
                Scoreboard previous =  Main.scoreboards.get(p);
                if (previous != null) {
                    previous.hideFor(p);
                }
                Main.scoreboards.remove(p);
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        Main.scoreboards.remove(e.getPlayer());
    }
}
