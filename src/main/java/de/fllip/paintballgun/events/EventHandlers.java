package de.fllip.paintballgun.events;

import de.fllip.paintballgun.BlockInfo;
import de.fllip.paintballgun.LocationUtils;
import de.fllip.paintballgun.PaintballGun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.07.2019
 * Time: 19:50
 */
public class EventHandlers extends BukkitRunnable implements Listener {

    private final static String PAINTBALL_GUN_SNOWBALL = "PAINTBALL_GUN_SNOWBALL";

    private final static int BLOCK_RADIUS = 3;
    private final static int SECONDS_TO_REVERT_BACK = 5;
    private final static Material MATERIAL_ID = Material.WOOL;

    private MetadataValue fixedMetadataValue = new FixedMetadataValue(PaintballGun.getInstance(), true);

    private Map<Location, BlockInfo> blockMap = new HashMap<>();

    @Override
    public void run() {
        updateBlocks();
    }

    @EventHandler
    private void handlePlayerInteractEvent(PlayerInteractEvent evt) {
        if (!isPaintballGunSpawnEvent(evt))
            return;

        Vector velocity = evt.getPlayer().getEyeLocation().getDirection().multiply(2);
        Snowball snowball = evt.getPlayer().launchProjectile(Snowball.class, velocity);
        snowball.setMetadata(PAINTBALL_GUN_SNOWBALL, fixedMetadataValue);
    }

    @EventHandler
    private void handleSnowballCollision(ProjectileHitEvent evt) {
        if (!isPaintballGunCollisionEvent(evt))
            return;

        Location hitLocation = getHitLocation(evt);
        getFilteredBlocksInRadius(hitLocation, BLOCK_RADIUS)
                .forEach(this::addBlockToMap);
    }

    private void updateBlocks() {
        blockMap.forEach((location, blockInfo) -> {
            blockInfo.decreaseTimer(PaintballGun.TICKS_TO_UPDATE / PaintballGun.TICKS_PER_SECOND);
            if (blockInfo.getTimerInSeconds() <= 0)
                revertBlock(location);
        });

        blockMap.entrySet().removeIf(entry -> entry.getValue().getTimerInSeconds() <= 0);
    }

    private void addBlockToMap(Block block) {
        if (blockMap.containsKey(block.getLocation())) {
            blockMap.get(block.getLocation()).setTimerInSeconds(SECONDS_TO_REVERT_BACK);
            return;
        }

        BlockInfo info = new BlockInfo(block.getLocation(), block.getState().getData().clone(), block.getType(), SECONDS_TO_REVERT_BACK);
        blockMap.put(block.getLocation(), info);
        block.setType(MATERIAL_ID);
    }

    private void revertBlock(Location location) {
        BlockInfo info = blockMap.get(location);
        info.getLocation().getBlock().setType(info.getMaterial());
        BlockState state = info.getLocation().getBlock().getState();
        state.setData(info.getOriginalMaterialData());
        state.update(true);
    }

    private Location getHitLocation(ProjectileHitEvent evt) {
        Location hitLocation = evt.getEntity().getLocation();
        Vector velocity = evt.getEntity().getVelocity().normalize();
        return hitLocation.add(velocity);
    }

    private List<Block> getFilteredBlocksInRadius(Location location, int radius) {
        List<Block> blocks = getBlocksInRadius(location, radius);
        return blocks.stream()
                .filter(block -> block.getType() != Material.AIR)
                .collect(Collectors.toList());
    }

    private List<Block> getBlocksInRadius(Location center, int radius) {
        return LocationUtils.getLocationsInCube(center, radius)
                .stream()
                .map(Location::getBlock)
                .collect(Collectors.toList());
    }

    private boolean isPaintballGunCollisionEvent(ProjectileHitEvent evt) {
        return evt.getEntity().hasMetadata(PAINTBALL_GUN_SNOWBALL);
    }

    private boolean isPaintballGunSpawnEvent(PlayerInteractEvent evt) {
        ItemStack item = evt.getPlayer().getItemInHand();

        if (item == null)
            return false;

        // todo: make item type (or id) configurable
        if (!item.getType().equals(Material.STICK))
            return false;

        return true;
    }


}
