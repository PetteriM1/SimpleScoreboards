package me.petterim1.scoreboards;

import cn.nukkit.Player;

import com.creeperface.nukkit.placeholderapi.PlaceholderAPIIml;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

public class ScoreboardUpdater extends Thread {

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
            PlaceholderAPI api = PlaceholderAPIIml.getInstance();

            Main.config.getStringList("text").forEach((text) -> {
                String money = "null";
                try {
                    Class.forName("me.onebone.economyapi.EconomyAPI");
                    money = Double.toString(me.onebone.economyapi.EconomyAPI.getInstance().myMoney(p));
                } catch (Exception ex) {}
                scoreboardDisplay.addLine(api.translateString(text, p).replaceAll("§", "\u00A7").replaceAll("%economy_money%", money), line++);
            });

            scoreboard.showFor(p);
            Main.scoreboards.put(p, scoreboard);
            line = 0;
        }
    }
}