package de.fllip.paintballgun;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.07.2019
 * Time: 21:33
 */
public class LocationUtils {

    public static List<Location> getLocationsInCube(Location center, int radius) {
        List<Location> blocks = new ArrayList<>();
        for (int x = -(radius); x <= radius; x++) {
            for (int y = -(radius); y <= radius; y++) {
                for (int z = -(radius); z <= radius; z++) {
                    blocks.add(center.getBlock().getRelative(x, y, z).getLocation());
                }
            }
        }
        return blocks;
    }

    public static List<Location> getLocationsInSphere(Location location, double radiusX, double radiusY, double radiusZ, boolean filled) {
        Vector pos = location.toVector();
        World world = location.getWorld();
        List<Location> blocks = new ArrayList<Location>();

        radiusX += 0.5;
        radiusY += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX:
        for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY:
            for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ:
                for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (!filled) {
                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            continue;
                        }
                    }

                    blocks.add(pos.add(new Vector(x, y, z)).toLocation(world));
                    blocks.add(pos.add(new Vector(-x, y, z)).toLocation(world));
                    blocks.add(pos.add(new Vector(x, -y, z)).toLocation(world));
                    blocks.add(pos.add(new Vector(x, y, -z)).toLocation(world));
                    blocks.add(pos.add(new Vector(-x, -y, z)).toLocation(world));
                    blocks.add(pos.add(new Vector(x, -y, -z)).toLocation(world));
                    blocks.add(pos.add(new Vector(-x, y, -z)).toLocation(world));
                    blocks.add(pos.add(new Vector(-x, -y, -z)).toLocation(world));
                }
            }
        }

        return blocks;
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }
}
