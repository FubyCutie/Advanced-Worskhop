package com.blackoutburst.workshop.commands;

import com.blackoutburst.workshop.Main;
import com.blackoutburst.workshop.core.PlayArea;
import com.blackoutburst.workshop.core.WSPlayer;
import com.blackoutburst.workshop.utils.GameUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Play implements CommandExecutor {

    private void setCraftAmount(WSPlayer wsplayer, String value) {
        try {
            int limit = Integer.parseInt(value);

            if (limit <= 0) {
                wsplayer.getGameOptions().setUnlimitedCrafts(true);
            } else {
                wsplayer.getGameOptions().setUnlimitedCrafts(false);
                wsplayer.getGameOptions().setCraftLimit(limit);
            }
        } catch (Exception ignored) {
            wsplayer.getPlayer().sendMessage("§cInvalid craft amount provided, using your current settings.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            WSPlayer wsplayer = WSPlayer.getFromPlayer((Player) sender);
            if (wsplayer == null || wsplayer.isInGame()) return true;

            wsplayer.getPlayer().sendMessage("Game running");

            for (PlayArea area : Main.playAreas) {
                if (area.isBusy()) continue;
                if (args.length > 0 && !args[0].equals(area.getType())) continue;
                area.setBusy(true);
                wsplayer.setPlayArea(area);
                GameUtils.loadCraft(wsplayer, area.getType());
                GameUtils.loadMaterials(wsplayer, area.getType());
                GameUtils.spawnEntities(wsplayer, area.getType());
                GameUtils.startRound(wsplayer);
                wsplayer.setInGame(true);
                wsplayer.getPlayer().setGameMode(GameMode.SURVIVAL);
                wsplayer.getBoard().set(wsplayer.getPlayer(), 13, "Map: §e" + area.getType());
                wsplayer.setCurrentCraftIndex(0);

                if (args.length > 1)
                    setCraftAmount(wsplayer, args[1]);

                return true;
            }
            wsplayer.getPlayer().sendMessage("No game available");
        }

        return true;
    }
}
