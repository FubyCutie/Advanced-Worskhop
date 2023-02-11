package com.blackoutburst.workshop.core;

import com.blackout.npcapi.core.NPC;
import com.blackoutburst.workshop.Craft;
import com.blackoutburst.workshop.Main;
import com.blackoutburst.workshop.nms.NMSEntities;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WSPlayer {

    protected boolean nextRound;

    protected Player player;

    protected String inventoryType;

    protected NMSEntities[] itemFrames = new NMSEntities[9];

    protected List<NPC> npcs = new ArrayList<>();

    protected Location scanWand1;
    protected Location scanWand2;

    protected PlayArea playArea;

    protected List<Craft> crafts = new ArrayList<>();

    protected List<MaterialBlock> materialBlocks = new ArrayList<>();

    protected boolean inGame = false;
    protected Craft currentCraft = null;

    public WSPlayer(Player player) {
        this.player = player;
    }

    public static WSPlayer getFromPlayer(Player p) {
        int size = Main.players.size();

        for (int i = 0; i < size; i++) {
            WSPlayer qp = Main.players.get(i);
            if (qp == null) break;
            if (qp.player.getUniqueId().equals(p.getUniqueId())) {
                return (qp);
            }
        }
        return (null);
    }

    public Craft getCurrentCraft() {
        return currentCraft;
    }

    public boolean isInGame() {
        return inGame;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentCraft(Craft currentCraft) {
        this.currentCraft = currentCraft;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Location getScanWand1() {
        return scanWand1;
    }

    public Location getScanWand2() {
        return scanWand2;
    }

    public void setScanWand1(Location scanWand1) {
        this.scanWand1 = scanWand1;
    }

    public void setScanWand2(Location scanWand2) {
        this.scanWand2 = scanWand2;
    }

    public List<Craft> getCrafts() {
        return crafts;
    }

    public PlayArea getPlayArea() {
        return playArea;
    }

    public void setPlayArea(PlayArea playArea) {
        this.playArea = playArea;
    }

    public List<MaterialBlock> getMaterialBlocks() {
        return materialBlocks;
    }

    public List<NPC> getNpcs() {
        return npcs;
    }

    public boolean isNextRound() {
        return nextRound;
    }

    public void setNextRound(boolean nextRound) {
        this.nextRound = nextRound;
    }

    public NMSEntities[] getItemFrames() {
        return itemFrames;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }
}
