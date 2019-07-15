package de.fllip.paintballgun;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.07.2019
 * Time: 21:42
 */
@AllArgsConstructor
@Getter
public class BlockInfo {

    private Location location;
    private MaterialData originalMaterialData;
    private Material material;

    @Setter
    private int timerInSeconds;

    public void decreaseTimer(int value) {
        timerInSeconds -= value;
    }
}
