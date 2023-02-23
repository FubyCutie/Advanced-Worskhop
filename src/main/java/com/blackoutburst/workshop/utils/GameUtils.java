package com.blackoutburst.workshop.utils;

import com.blackout.npcapi.core.NPC;
import com.blackout.npcapi.utils.NPCManager;
import com.blackout.npcapi.utils.SkinLoader;
import com.blackoutburst.workshop.Craft;
import com.blackoutburst.workshop.Main;
import com.blackoutburst.workshop.core.*;
import com.blackoutburst.workshop.core.blocks.MaterialBlock;
import com.blackoutburst.workshop.nms.*;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameUtils {

    public static void endGame(WSPlayer wsplayer) {
        wsplayer.setInGame(false);
        wsplayer.getGamestarter().cancel();
        wsplayer.setCurrentCraft(null);
        wsplayer.setCraftList(null);

        wsplayer.getBoard().set(wsplayer.getPlayer(), 14, "Map: §enone");
        wsplayer.getBoard().set(wsplayer.getPlayer(), 10, "Craft: §enone");
        wsplayer.getBoard().set(wsplayer.getPlayer(), 7, "Round: §enone");
        wsplayer.getBoard().set(wsplayer.getPlayer(), 6, "Remaining Time: §eN/A");

        MapUtils.restoreArea(wsplayer, true);

        PlayArea area = wsplayer.getPlayArea();
        int craftNumber = wsplayer.getCurrentCraftIndex() - 1;
        if (area != null)
            area.setBusy(false);

        String output = "";
        Float duration = 0.0f;

        if (wsplayer.getTimers().getMapBegin() != null && !wsplayer.getGameOptions().isTimeLimited()) {
            duration = Duration.between(wsplayer.getTimers().getMapBegin(), wsplayer.getTimers().getMapEnd()).toMillis() / 1000.0f;
            output = StringUtils.ROUND.format(duration) + "s";
        }
        if (wsplayer.getGameOptions().isTimeLimited()) {
            output = craftNumber + " craft" + (craftNumber == 1 ? "" : "s");
            wsplayer.getGameOptions().setTimeLimited(false);
            wsplayer.getGameOptions().setUnlimitedCrafts(false);
        }

        wsplayer.getPlayer().sendMessage("§eThe game ended! §b(" + output + ")");
        wsplayer.getPlayer().setGameMode(GameMode.ADVENTURE);

        for (NPC npc : wsplayer.getNpcs()) {
            NPCManager.deleteNPC(wsplayer.getPlayer(), npc);
        }
        wsplayer.getNpcs().clear();

        for (NMSEntities entity : wsplayer.getEntities()) {
            NMSEntityDestroy.send(wsplayer.getPlayer(), entity.getID());
        }
        wsplayer.getNpcs().clear();

        for (NMSEntities frame : wsplayer.getItemFrames()) {
            if (frame == null) continue;
            NMSEntityDestroy.send(wsplayer.getPlayer(), frame.getID());
        }
        wsplayer.getPlayer().teleport(Main.spawn);

        wsplayer.getPlayer().getInventory().clear();
        wsplayer.setWaiting(false);
        wsplayer.getTimers().setMapBegin(null);

        wsplayer.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
        wsplayer.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
        wsplayer.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
        wsplayer.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
    }

    private static void fastCook(Furnace furnace, ItemStack stack, Material output, int data) {
        furnace.getInventory().setSmelting(new ItemStack(Material.AIR));
        furnace.getInventory().setResult(new ItemStack(output, stack.getAmount(), (short) data));
    }

    public static void instantSmelt(Furnace furnace) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 200) {
                    this.cancel();
                }
                ItemStack stack = furnace.getInventory().getSmelting();
                if (stack != null) {
                    switch (stack.getType()) {
                        case POTATO_ITEM:
                            fastCook(furnace, stack, Material.BAKED_POTATO, 0);
                            break;
                        case RAW_CHICKEN:
                            fastCook(furnace, stack, Material.COOKED_CHICKEN, 0);
                            break;
                        case RAW_FISH:
                            fastCook(furnace, stack, Material.COOKED_FISH, 0);
                            break;
                        case PORK:
                            fastCook(furnace, stack, Material.GRILLED_PORK, 0);
                            break;
                        case RAW_BEEF:
                            fastCook(furnace, stack, Material.COOKED_BEEF, 0);
                            break;
                        case CLAY_BALL:
                            fastCook(furnace, stack, Material.CLAY_BRICK, 0);
                            break;
                        case SAND:
                            fastCook(furnace, stack, Material.GLASS, 0);
                            break;
                        case GOLD_ORE:
                            fastCook(furnace, stack, Material.GOLD_INGOT, 0);
                            break;
                        case IRON_ORE:
                            fastCook(furnace, stack, Material.IRON_INGOT, 0);
                            break;
                        case NETHERRACK:
                            fastCook(furnace, stack, Material.NETHER_BRICK_ITEM, 0);
                            break;
                        case COBBLESTONE:
                            fastCook(furnace, stack, Material.STONE, 0);
                            break;
                        case CACTUS:
                            fastCook(furnace, stack, Material.INK_SACK, 2);
                            break;
                        case LOG:
                        case LOG_2:
                            fastCook(furnace, stack, Material.COAL, 1);
                            break;
                        case COAL_ORE:
                            fastCook(furnace, stack, Material.COAL, 0);
                            break;
                        case DIAMOND_ORE:
                            fastCook(furnace, stack, Material.DIAMOND, 0);
                            break;
                        case EMERALD_ORE:
                            fastCook(furnace, stack, Material.EMERALD, 0);
                            break;
                        case LAPIS_ORE:
                            fastCook(furnace, stack, Material.INK_SACK, 4);
                            break;
                        case QUARTZ_ORE:
                            fastCook(furnace, stack, Material.QUARTZ, 0);
                            break;
                        case REDSTONE_ORE:
                            fastCook(furnace, stack, Material.REDSTONE, 0);
                            break;
                    }
                }
                count++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1L, 0L);
    }


    private static void teleportPlayerToArea(Player player, String[] data, PlayArea area) {
        float x = Integer.parseInt(data[2]) + area.getAnchor().getBlockX() + 0.5f;
        int y = Integer.parseInt(data[3]) + area.getAnchor().getBlockY();
        float z = Integer.parseInt(data[4]) + area.getAnchor().getBlockZ() + 0.5f;
        int yaw = 0;
        switch (BlockFace.valueOf(data[5])) {
            case NORTH:
                yaw = 180;
                break;
            case SOUTH:
                yaw = 0;
                break;
            case EAST:
                yaw = -90;
                break;
            case WEST:
                yaw = 90;
                break;
        }

        player.teleport(new Location(player.getWorld(), x, y, z, yaw, 0));
    }

    private static void spawnNPC(String name, int skinId, WSPlayer wsPlayer, String[] data, PlayArea area) {
        float x = Integer.parseInt(data[2]) + area.getAnchor().getBlockX() + 0.5f;
        int y = Integer.parseInt(data[3]) + area.getAnchor().getBlockY();
        float z = Integer.parseInt(data[4]) + area.getAnchor().getBlockZ() + 0.5f;
        int yaw = 0;
        switch (BlockFace.valueOf(data[5])) {
            case NORTH:
                yaw = 180;
                break;
            case SOUTH:
                yaw = 0;
                break;
            case EAST:
                yaw = -90;
                break;
            case WEST:
                yaw = 90;
                break;
        }

        NPC npc = new NPC(UUID.randomUUID(), name)
                .setLocation(new Location(wsPlayer.getPlayer().getWorld(), x, y, z, yaw, 0))
                .setSkin(SkinLoader.getSkinById(skinId))
                .setNameVisible(false)
                .setCapeVisible(false);

        NPCManager.spawnNPC(npc, wsPlayer.getPlayer());
        wsPlayer.getNpcs().add(npc);
    }

    private static void spawnEntity(NMSEntities.EntityType type, WSPlayer wsPlayer, String[] data, PlayArea area) {
        float x = Integer.parseInt(data[2]) + area.getAnchor().getBlockX() + 0.5f;
        int y = Integer.parseInt(data[3]) + area.getAnchor().getBlockY();
        float z = Integer.parseInt(data[4]) + area.getAnchor().getBlockZ() + 0.5f;
        int yaw = 0;
        switch (BlockFace.valueOf(data[5])) {
            case NORTH:
                yaw = 180;
                break;
            case SOUTH:
                yaw = 0;
                break;
            case EAST:
                yaw = -90;
                break;
            case WEST:
                yaw = 90;
                break;
        }

        NMSEntities entity = new NMSEntities(wsPlayer.getPlayer().getWorld(), NMSEntities.EntityType.CHICKEN);
        entity.setLocation(x, y, z, yaw, 0);
        NMSSpawnEntityLiving.send(wsPlayer.getPlayer(), entity);
        NMSEntityHeadRotation.send(wsPlayer.getPlayer(), entity, yaw);
        wsPlayer.getEntities().add(entity);
    }

    private static void spawnItemFrame(WSPlayer wsPlayer, String[] data, PlayArea area, int id) {
        int x = Integer.parseInt(data[2]) + area.getAnchor().getBlockX();
        int y = Integer.parseInt(data[3]) + area.getAnchor().getBlockY();
        int z = Integer.parseInt(data[4]) + area.getAnchor().getBlockZ();
        NMSEnumDirection.Direction direction = NMSEnumDirection.Direction.NORTH;
        int logicalDirection = 0;

        switch (BlockFace.valueOf(data[5])) {
            case NORTH:
                direction = NMSEnumDirection.Direction.NORTH;
                logicalDirection = 2;
                break;
            case SOUTH:
                direction = NMSEnumDirection.Direction.SOUTH;
                logicalDirection = 0;
                break;
            case EAST:
                direction = NMSEnumDirection.Direction.EAST;
                logicalDirection = 3;
                break;
            case WEST:
                direction = NMSEnumDirection.Direction.WEST;
                logicalDirection = 1;
                break;
        }

        NMSBlockPosition position = new NMSBlockPosition(x, y, z);
        NMSEnumDirection facingDirection = new NMSEnumDirection(direction);
        NMSEntities itemFrame = new NMSEntities(wsPlayer.getPlayer().getWorld(), NMSEntities.EntityType.ITEM_FRAME, position.position, facingDirection.direction);
        NMSSpawnEntity.send(wsPlayer.getPlayer(), itemFrame, logicalDirection);
        NMSEntityMetadata.send(wsPlayer.getPlayer(), itemFrame);
        wsPlayer.getItemFrames()[id] = itemFrame;
    }

    public static void spawnEntities(WSPlayer wsPlayer, String type) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("./plugins/Workshop/" + type + ".logic"));
            PlayArea area = wsPlayer.getPlayArea();

            for (String line : lines) {
                if (line.startsWith("S")) {
                    String[] data = line.split(", ");

                    if (data[1].equals("player")) {
                        teleportPlayerToArea(wsPlayer.getPlayer(), data, area);
                    }

                    if (data[1].equals("chicken")) {
                        spawnEntity(NMSEntities.EntityType.CHICKEN, wsPlayer, data, area);
                    }

                    if (data[1].equals("villager")) {
                        int skinID = SkinLoader.skins.size() + 1;
                        SkinLoader.loadSkinFromUUID(skinID, wsPlayer.getPlayer().getUniqueId().toString().replace("-", ""));
                        spawnNPC("villager", skinID, wsPlayer, data, area);
                    }

                    if (data[1].equals("0") || data[1].equals("1") || data[1].equals("2") || data[1].equals("3") ||
                            data[1].equals("4") || data[1].equals("5") || data[1].equals("6") ||
                            data[1].equals("7") || data[1].equals("8") || data[1].equals("9")) {
                        spawnItemFrame(wsPlayer, data, area, Integer.parseInt(data[1]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWand(PlayerInventory inv) {
        ItemStack item = inv.getItemInHand();

        return (item.getType() == Material.BLAZE_ROD &&
                item.getItemMeta().getDisplayName().equals("§6Scan wand"));
    }

    public static void startRound(WSPlayer wsplayer) {
        if (!wsplayer.isInGame()) return;
        Player player = wsplayer.getPlayer();

        player.getInventory().clear();

        wsplayer.setWaiting(false);

        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

        player.sendMessage("§eYou need to craft a §r" + wsplayer.getCurrentCraft().getName());

        wsplayer.getBoard().set(player, 10, "Craft: §e" + wsplayer.getCurrentCraft().getName());
        wsplayer.getBoard().set(player, 7, "Round: §e" + StringUtils.getCurrentRound(wsplayer));

        NMSEntities outputFrame = wsplayer.getItemFrames()[0];
        if (outputFrame != null) {
            ItemStack outputItem = wsplayer.getCurrentCraft().getItemRequired();
            NMSItemFrame.setItem(player, outputFrame, outputItem);
        }

        for (int i = 0; i < 9; i++) {
            NMSEntities frame = wsplayer.getItemFrames()[i + 1];
            ItemStack item = wsplayer.getCurrentCraft().getCraftingTable()[i];
            NMSItemFrame.setItem(player, frame, item);
        }
        MapUtils.restoreArea(wsplayer, false);
        wsplayer.getTimers().setRoundBegin(Instant.now());

        Integer roundCount = DBUtils.getData(wsplayer.getPlayer(), "roundCount", Integer.class);
        DBUtils.saveData(wsplayer.getPlayer(), "roundCount", roundCount != null ? roundCount + 1 : 1, Integer.class);

        Integer mapRoundCount = DBUtils.getData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".roundCount", Integer.class);
        DBUtils.saveData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".roundCount", mapRoundCount != null ? mapRoundCount + 1 : 1, Integer.class);
    }

    private static ItemStack getItem(String data) {
        if (data.contains(":")) {
            String[] subData = data.split(":");
            return new ItemStack(Integer.parseInt(subData[0]), 1, Short.parseShort(subData[1]));
        }
        return new ItemStack(Integer.parseInt(data));
    }

    public static MaterialBlock getMaterialBlock(WSPlayer wsPlayer, Location location) {
        for (MaterialBlock block : wsPlayer.getMaterialBlocks()) {
            if (location.getWorld().getName().equals(block.getWorld().getName()) &&
                    location.getBlockX() == block.getLocation().getBlockX() &&
                    location.getBlockY() == block.getLocation().getBlockY() &&
                    location.getBlockZ() == block.getLocation().getBlockZ()) {
                return block;
            }
        }
        return null;
    }

    public static void loadMaterials(WSPlayer wsPlayer, String type) {
        wsPlayer.getMaterialBlocks().clear();
        PlayArea area = wsPlayer.getPlayArea();
        if (area == null) return;

        try {
            List<String> lines = Files.readAllLines(Paths.get("./plugins/Workshop/" + type + ".logic"));

            for (String line : lines) {

                if ((line.startsWith("R")) || (line.startsWith("P"))) {
                    String[] data = line.split(";",-1);

                    int relX = Integer.parseInt(data[1].split(",")[0]);
                    int relY = Integer.parseInt(data[1].split(",")[1]);
                    int relZ = Integer.parseInt(data[1].split(",")[2]);

                    String[] items = data[2].split(",");
                    String[] neededItems = {};
                    List<List<String>> allTools = new ArrayList<>();

                    if (!(data[3].equals(""))) {
                        neededItems = data[3].split(",");
                    }
                    if (!(data[4].equals(""))) {
                        for (int i = 4; i < data.length; i++) {
                            List<String> toolsList = Arrays.asList(data[i].split(","));

                            allTools.add(toolsList);
                        }
                    }

                    allTools = MiscUtils.transpose2dList(allTools);

                    String[][] allToolsArray = new String[allTools.size()][];

                    for (List<String> tools : allTools) {
                        String[] toolArray = new String[tools.size()];
                        for (String tool : tools) {
                            toolArray[tools.indexOf(tool)] = tool;
                        }
                        allToolsArray[allTools.indexOf(tools)] = toolArray;
                    }

                    List<Material> allItems = new ArrayList<>();
                    List<Byte> allItemData = new ArrayList<>();

                    for (String item : items) {
                        String id = item.split(" ")[0];
                        int itemData = Integer.parseInt(item.split(" ")[1]);

                        ReadWriteNBT nbtObject = NBT.createNBTObject();
                        nbtObject.setString("id",id);
                        nbtObject.setInteger("Damage",itemData);
                        nbtObject.setInteger("Count",1);
                        ItemStack convertedItem = NBTItem.convertNBTtoItem((NBTCompound) nbtObject);
                        allItems.add(convertedItem.getType());
                        allItemData.add(convertedItem.getData().getData());
                    }
                    if ((neededItems.length != 0) && !(neededItems[0].equals(""))) {
                        for (String item : neededItems) {
                            String id = item.split(" ")[0];
                            int itemData = Integer.parseInt(item.split(" ")[1]);

                            ReadWriteNBT nbtObject = NBT.createNBTObject();
                            nbtObject.setString("id", id);
                            nbtObject.setInteger("Damage", itemData);
                            nbtObject.setInteger("Count", 1);
                            ItemStack convertedItem = NBTItem.convertNBTtoItem((NBTCompound) nbtObject);
                            allItems.add(convertedItem.getType());
                            allItemData.add(convertedItem.getData().getData());
                        }
                    }
                    Material[] itemArray = allItems.toArray(new Material[]{});
                    Byte[] dataArray = allItemData.toArray(new Byte[]{});

                    Location offset = area.getAnchor();
                    Location relLoc = new Location(area.getAnchor().getWorld(), relX, relY, relZ);
                    Location location = relLoc.add(offset);

                    wsPlayer.getMaterialBlocks().add(new MaterialBlock(itemArray, dataArray, location, location.getWorld(), allToolsArray, 0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCraft(WSPlayer wsPlayer, String type) {
        wsPlayer.getCrafts().clear();

        String name = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get("./plugins/Workshop/" + type + ".craft"));

            for (String line : lines) {
                String[] data = line.split(", ");
                name = data[0];
                ItemStack requiredItem = getItem(data[1]);

                ItemStack[] craftingTable = new ItemStack[]{
                        getItem(data[2]), getItem(data[3]), getItem(data[4]),
                        getItem(data[5]), getItem(data[6]), getItem(data[7]),
                        getItem(data[8]), getItem(data[9]), getItem(data[10])
                };

                List<ItemStack> materials = new ArrayList<>();
                for (int i = 11; i < data.length; i++) {
                    String[] subData = data[i].split(":");
                    Material itemType = Material.getMaterial(Integer.parseInt(subData[0]));
                    int itemAmount = Integer.parseInt(subData[2]);
                    short itemData = Short.parseShort(subData[1]);

                    materials.add(new ItemStack(itemType, itemAmount, itemData));
                }

                wsPlayer.getCrafts().add(new Craft(name, requiredItem, craftingTable, materials));
            }
        } catch (Exception e) {
            Bukkit.broadcastMessage("Error loading craft: " + name);
            e.printStackTrace();
        }
    }

    private static boolean hasLostSupport(char direction, boolean solid, int id, byte data) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("./plugins/Workshop/supports.txt"));
            String pattern = direction + ",([0-9]+):?([0-9]+)?";

            Pattern regexPattern = Pattern.compile(pattern);

            if ((id == 132) && (!solid)) {
                return false;
            }

            for (String line : lines) {
                Matcher patternMatcher = regexPattern.matcher(line);
                if (!patternMatcher.matches()) {
                    continue;
                }
                int checkID = Integer.parseInt(patternMatcher.group(1));

                if (!(checkID == id)) {
                    continue;
                }
                if (patternMatcher.group(2) == null) {
                    return true;
                }
                byte checkData = (byte) Integer.parseInt(patternMatcher.group(2));

                if (data == checkData) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void supportIterator (Location location, WSPlayer wsplayer, char direction) {
        Player player = wsplayer.getPlayer();
        Block supporter = location.getWorld().getBlockAt(location);

        boolean solid = supporter.getType().isSolid();

        char[] directions = {'U','N','E','S','W','D'};
        char[] oppositeDirections = {'D','S','W','N','E','U'};
        int[][] relativeCoords = {
                {0,1,0},
                {0,0,-1},
                {1,0,0},
                {0,0,1},
                {-1,0,0},
                {0,-1,0}
        };

        for (int i = 0; i < directions.length; i++) {
            int xChange = relativeCoords[i][0];
            int yChange = relativeCoords[i][1];
            int zChange = relativeCoords[i][2];
            Location newLocation = location.clone();
            newLocation.add(xChange,yChange,zChange);

            Block testBlock = newLocation.getWorld().getBlockAt(newLocation);
            int id = testBlock.getTypeId();
            byte data = testBlock.getData();

            if (hasLostSupport(directions[i], solid, id, data) && direction != oppositeDirections[i]) {
                MaterialBlock materialBlock = GameUtils.getMaterialBlock(wsplayer, newLocation);

                if (materialBlock != null) {
                    ItemStack item = new ItemStack(materialBlock.getType(), 1, materialBlock.getData());
                    player.getInventory().addItem(item);
                }
                supportIterator (newLocation, wsplayer, directions[i]);
            }
        }
    }

    public static boolean canBreak(MaterialBlock materialBlock, Player player) {
        if (WSPlayer.getFromPlayer(player).isWaiting()) { return false; }
        if (materialBlock.getTools()[0].isEmpty()) { return true; }
        for (String tool : materialBlock.getTools()) {
            if (player.getItemInHand().getType() == Material.AIR) {
                return false;
            }
            NBTContainer handItemNBT = NBTItem.convertItemtoNBT(player.getItemInHand());
            String handItem = handItemNBT.getString("id");
            if (handItem.equals(tool)) {
                return true;
            }
        }
        return false;
    }

    public static boolean prepareNextRound(WSPlayer wsplayer) {
        wsplayer.setWaiting(true);
        GameOptions gameoptions = wsplayer.getGameOptions();
        Timers timers = wsplayer.getTimers();
        if (!gameoptions.isUnlimitedCrafts() && wsplayer.getCurrentCraftIndex() >= gameoptions.getCraftLimit()) {
            wsplayer.getTimers().setMapEnd(Instant.now());
            if (wsplayer.getCurrentCraftIndex() == 5 && gameoptions.getRandomType() == 'N') {
                Float duration = Duration.between(timers.getMapBegin(), timers.getMapEnd()).toMillis() / 1000.0f;

                Double currentDuration = DBUtils.getData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".time", Double.class);
                if (currentDuration == null) currentDuration = Double.MAX_VALUE;

                if (duration < currentDuration)
                    DBUtils.saveData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".time", duration, Float.class);

                if (currentDuration != Double.MAX_VALUE && (duration < currentDuration)) {
                    Webhook.send("**"+ wsplayer.getPlayer().getName() + "** got a new PB on the map **" + wsplayer.getPlayArea().getType() + "**!\\nTime: **" +StringUtils.ROUND.format( duration) + "s**\\nImprovement: **" + StringUtils.ROUND.format(duration - currentDuration) + "s**");
                    wsplayer.getPlayer().sendMessage("§d§lPB! (" + StringUtils.ROUND.format(duration - currentDuration) + "s)");
                }
                if (gameoptions.isShowNonPBs() && duration >= currentDuration) {
                    wsplayer.getPlayer().sendMessage("§c§l(+" + StringUtils.ROUND.format(duration - currentDuration) + "s)");
                }
            }
            if (wsplayer.getCurrentCraftIndex() == wsplayer.getCrafts().size() && gameoptions.getRandomType() == 'N') {
                Float duration = Duration.between(timers.getMapBegin(), timers.getMapEnd()).toMillis() / 1000.0f;

                Double currentDuration = DBUtils.getData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".timeAll", Double.class);
                if (currentDuration == null) currentDuration = Double.MAX_VALUE;

                if (duration < currentDuration)
                    DBUtils.saveData(wsplayer.getPlayer(), wsplayer.getPlayArea().getType() + ".timeAll", duration, Float.class);

                if (currentDuration != Double.MAX_VALUE && (duration < currentDuration)) {
                    Webhook.send("**"+ wsplayer.getPlayer().getName() + "** got a new PB on the map **" + wsplayer.getPlayArea().getType() + "** for all crafts!\\nTime: **" +StringUtils.ROUND.format( duration) + "s**\\nImprovement: **" + StringUtils.ROUND.format(duration - currentDuration) + "s**");
                    wsplayer.getPlayer().sendMessage("§d§lPB! (" + StringUtils.ROUND.format(duration - currentDuration) + "s" + ")");
                }
                if (gameoptions.isShowNonPBs() && duration >= currentDuration) {
                    wsplayer.getPlayer().sendMessage("§d§l(+" + StringUtils.ROUND.format(duration - currentDuration) + "s)");
                }
            }
            endGame(wsplayer);
            return true;
        }

        int craftIndex = wsplayer.getCurrentCraftIndex();
        int bagSize = wsplayer.getGameOptions().getBagSize();

        if (craftIndex == bagSize) {
            updateCraftList(wsplayer);
        }

        updateCraft(wsplayer);
        return false;
    }
    public static void updateCraft(WSPlayer wsplayer) {
        List<Craft> crafts = wsplayer.getCraftList();
        int craftIndex = wsplayer.getCurrentCraftIndex() + 1;
        wsplayer.setCurrentCraft(crafts.get((craftIndex - 1) % wsplayer.getGameOptions().getBagSize()));
        wsplayer.setCurrentCraftIndex(craftIndex);
    }

    public static void updateCraftList(WSPlayer wsplayer) {
        GameOptions gameoptions = wsplayer.getGameOptions();
        char type = gameoptions.getRandomType();
        List<Craft> validCrafts = wsplayer.getCrafts();
        int craftAmount = validCrafts.size();
        int bagSize = gameoptions.getBagSize();
        List<Craft> finalCraftList =  new ArrayList<>();
        Random rng = new Random();

        switch (type) {
            case 'N':
            case 'B':
                List<Craft> bags = new ArrayList<>();
                if (wsplayer.getCraftList() == null) {
                    List<Craft> bag1 = generateBag(wsplayer);
                    List<Craft> bag2 = generateBag(wsplayer);
                    bags.addAll(bag1);
                    bags.addAll(bag2);
                    finalCraftList = bags;
                    break;
                }
                List<Craft> tempCraftList = wsplayer.getCraftList().subList(bagSize,bagSize*2);
                List<Craft> bag = generateBag(wsplayer);
                finalCraftList.addAll(tempCraftList);
                finalCraftList.addAll(bag);
                break;
            case 'R':
                List<Craft> last5 = wsplayer.getCraftList().subList(5,10);
                finalCraftList.addAll(last5);
                for (int i = 0; i < 5; i++) {
                    int n = rng.nextInt(craftAmount);
                    finalCraftList.add(validCrafts.get(n));
                }
                break;
        }
        wsplayer.setCraftList(finalCraftList);
    }
    public static List<Craft> generateBag(WSPlayer wsplayer) {
        List<Craft> validCrafts = wsplayer.getCrafts();
        int craftAmount = validCrafts.size();
        int bagSize = wsplayer.getGameOptions().getBagSize();
        float craftCopies = (float) bagSize / craftAmount;
        int roundedCraftCopies = (int) Math.ceil(craftCopies);
        List<Craft> bag = new ArrayList<>();
        int extra = (roundedCraftCopies*craftAmount) - bagSize;

        for (int i = 0; i < roundedCraftCopies; i++) {
            bag.addAll(validCrafts);
        }
        Collections.shuffle(bag);
        bag = bag.subList(0, bag.size() - extra);

        return bag;
    }
}
