package com.blackoutburst.workshop;

import com.blackout.npcapi.utils.SkinLoader;
import com.blackoutburst.workshop.commands.*;
import com.blackoutburst.workshop.core.EventListener;
import com.blackoutburst.workshop.core.PlayArea;
import com.blackoutburst.workshop.core.WSPlayer;
import com.blackoutburst.workshop.utils.GameUtils;
import com.blackoutburst.workshop.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    public static Location spawn;

    public static List<PlayArea> playAreas = new ArrayList<>();

    public static List<WSPlayer> players = new ArrayList<>();

    @Override
    public void onEnable() {
        new File("./plugins/Workshop").mkdirs();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("play").setExecutor(new Play());
        getCommand("l").setExecutor(new L());
        getCommand("scanwand").setExecutor(new ScanWand());
        getCommand("logicscan").setExecutor(new LogicScan());
        getCommand("decoscan").setExecutor(new DecoScan());
        getCommand("pastemap").setExecutor(new PasteMap());
        getCommand("setspawn").setExecutor(new SetSpawn());
        getCommand("spawn").setExecutor(new Spawn());
        getCommand("recipeadd").setExecutor(new RecipeAdd());
        getCommand("setcraftlimit").setExecutor(new SetCraftLimit());
        getCommand("setcraftamount").setExecutor(new SetCraftLimit());
        getCommand("setunlimitedcraft").setExecutor(new SetUnlimitedCraft());
        MapUtils.loadPlayAreas();
        MapUtils.loadSpawn();
        SkinLoader.loadSkinFromUUID(0, "92deafa9430742d9b00388601598d6c0");

        new BukkitRunnable() {
            @Override
            public void run() {
                int size = Main.players.size();
                for (int i = 0; i < size; i++) {
                    WSPlayer wsPlayer = Main.players.get(i);
                    if (wsPlayer == null) break;
                    if (wsPlayer.isNextRound()) {
                        wsPlayer.setNextRound(false);

                        int round_delay = 1;

                        boolean finished = GameUtils.prepareNextRound(wsPlayer);

                        if (finished) { continue; }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                GameUtils.startRound(wsPlayer);
                            }
                        }.runTaskLater(Main.getPlugin(Main.class), round_delay * 20);
                    }

                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1L, 0L);
    }
}