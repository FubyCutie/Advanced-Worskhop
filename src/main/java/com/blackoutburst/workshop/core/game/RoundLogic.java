package com.blackoutburst.workshop.core.game;

import com.blackoutburst.workshop.core.WSPlayer;
import com.blackoutburst.workshop.utils.files.DBUtils;
import com.blackoutburst.workshop.utils.map.MapUtils;
import com.blackoutburst.workshop.utils.minecraft.ArmorUtils;
import com.blackoutburst.workshop.utils.minecraft.CraftUtils;
import com.blackoutburst.workshop.utils.minecraft.ItemFrameUtils;
import com.blackoutburst.workshop.utils.minecraft.ScoreboardUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoundLogic {

    private static void updateRoundData(WSPlayer wsplayer) {
        Integer roundCount = DBUtils.getData(wsplayer.getPlayer(), "roundCount", Integer.class);
        DBUtils.saveData(wsplayer.getPlayer(), "roundCount", roundCount != null ? roundCount + 1 : 1, Integer.class);

        Integer mapRoundCount = DBUtils.getData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".roundCount", Integer.class);
        DBUtils.saveData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".roundCount", mapRoundCount != null ? mapRoundCount + 1 : 1, Integer.class);
    }

    public static void startRound(WSPlayer wsplayer) {
        if (!wsplayer.isInGame()) return;

        Player player = wsplayer.getPlayer();
        if (player.getOpenInventory().getType() == InventoryType.WORKBENCH) {
            player.getOpenInventory().getTopInventory().clear();
        }
        player.getInventory().clear();
        if (wsplayer.getGameOptions().isHypixelSaysMode()) {
            ItemStack[] inv = wsplayer.getMapMeta().getInventoryContents();
            List<ItemStack> shuffledInv = new ArrayList<>(Arrays.stream(inv).toList());
            List<ItemStack> airList = new ArrayList<>();
            for (int i = 0; i < shuffledInv.size(); i++) {
                if (shuffledInv.get(i).getType() == Material.AIR) {
                    airList.add(shuffledInv.get(i));
                }
            }
            shuffledInv.removeAll(airList);
            if (wsplayer.getCustomRandom() != null) {
                Collections.shuffle(shuffledInv, wsplayer.getCustomRandom());
            } else {
                Collections.shuffle(shuffledInv);
            }

            ItemStack[] shuffledInvArray = shuffledInv.toArray(new ItemStack[0]);
            ItemStack[] finalInv = new ItemStack[36];

            if (shuffledInvArray.length > 27) {
                System.arraycopy(shuffledInvArray, 27, finalInv, 0, 9);
            }
            else {
                ItemStack[] airArray = Collections.nCopies(9, new ItemStack(Material.AIR)).toArray(new ItemStack[0]);
                System.arraycopy(airArray, 0, finalInv, 0, 9);
            }
            System.arraycopy(shuffledInvArray, 0, finalInv, 9, Integer.min(shuffledInvArray.length, 27));


            player.getInventory().setContents(finalInv);
        }
        if (player.getItemOnCursor().getAmount() != 0) wsplayer.setHasStored(true);
        player.sendMessage("§eYou need to craft a §r" + wsplayer.getCurrentCraft().getName());

        ArmorUtils.setArmor(player, wsplayer.getMapMeta());
        player.getInventory().setItemInOffHand(wsplayer.getMapMeta().getOffHand());
        ScoreboardUtils.startRound(wsplayer);
        ItemFrameUtils.updateCraft(wsplayer);

        MapUtils.restoreArea(wsplayer, false);

        wsplayer.setWaiting(false);
        wsplayer.getTimers().setRoundBegin(Instant.now());

        updateRoundData(wsplayer);
    }

    public static boolean prepareNextRound(WSPlayer wsplayer) {
        wsplayer.setWaiting(true);

        GameOptions gameoptions = wsplayer.getGameOptions();

        if (!gameoptions.isUnlimitedCrafts() && wsplayer.getCurrentCraftIndex() >= gameoptions.getCraftLimit()) {
            EndGameLogic.endGame(wsplayer, true);
            return true;
        }
        int craftIndex = wsplayer.getCurrentCraftIndex();
        int bagSize = wsplayer.getGameOptions().getBagSize();

        if (craftIndex % bagSize == 0) {
            CraftUtils.updateCraftList(wsplayer);
        }

        CraftUtils.updateCraft(wsplayer);
        return false;
    }

}
