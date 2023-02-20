package com.blackoutburst.workshop.core.events.listeners;

import com.blackout.npcapi.core.APlayer;
import com.blackout.npcapi.core.NPC;
import com.blackout.npcapi.core.NPCPacket;
import com.blackoutburst.workshop.core.Action;
import com.blackoutburst.workshop.core.NMSEntityInteractEvent;
import com.blackoutburst.workshop.core.NMSNPCInteractEvent;
import com.blackoutburst.workshop.core.WSPlayer;
import com.blackoutburst.workshop.core.events.nms.EntityInteract;
import com.blackoutburst.workshop.core.events.nms.NPCInteract;
import com.blackoutburst.workshop.nms.NMSEntities;
import com.blackoutburst.workshop.utils.DBUtils;
import com.blackoutburst.workshop.utils.EffectsUtils;
import com.blackoutburst.workshop.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;

public class NMSListener implements NPCPacket {

    @Override
    public void onLeftClick(Player player, int id) {
        APlayer ap = APlayer.get(player);
        for (NPC npc : ap.npcs) {
            if (id == npc.getEntityId()) {
                NPCInteract.execute(new NMSNPCInteractEvent(player, Action.LEFT_CLICK, npc));
                return;
            }
        }

        WSPlayer wp = WSPlayer.getFromPlayer(player);
        for (NMSEntities entity : wp.getEntities()) {
            if (id == entity.getID()) {
                EntityInteract.execute(new NMSEntityInteractEvent(player, Action.LEFT_CLICK, entity));
                return;
            }
        }
    }

    @Override
    public void onRightClick(Player player, int id) {
        APlayer ap = APlayer.get(player);
        for (NPC npc : ap.npcs) {
            if (id == npc.getEntityId()) {
                NPCInteract.execute(new NMSNPCInteractEvent(player, Action.RIGHT_CLICK, npc));
                return;
            }
        }

        WSPlayer wp = WSPlayer.getFromPlayer(player);
        for (NMSEntities entity : wp.getEntities()) {
            if (id == entity.getID()) {
                EntityInteract.execute(new NMSEntityInteractEvent(player, Action.RIGHT_CLICK, entity));
                return;
            }
        }
    }
}
