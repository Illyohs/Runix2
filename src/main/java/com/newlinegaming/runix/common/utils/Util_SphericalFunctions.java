package com.newlinegaming.runix.common.utils;

import java.util.HashSet;

import com.newlinegaming.runix.api.math.WorldPos;

import net.minecraft.world.World;

public class Util_SphericalFunctions {

    public static boolean radiusCheck(int x, int y, int z, int rd) {
        return ((x * x) + (y * y) + (z * z) < ((rd + 0.5) * (rd + 0.5)));
    }

    public static HashSet<WorldPos> getSphere(WorldPos coords, int radius) {
        float r_squared = (float) ((radius + 0.5) * (radius + 0.5));
        World world = coords.getWorld();
        HashSet<WorldPos> returnvalues = new HashSet<WorldPos>();
        // loop needs to cap at the top and bottom of the world
        int bottom = Math.max(-radius - 1, -1 * (coords.getY() - 1));
        int top = Math.min(radius + 1, (255 - coords.getY()));
        for (int y = bottom; y < top; y++) {
            for (int z = -radius - 1; z < radius + 1; z++) {
                for (int x = -radius - 1; x < radius + 1; x++) {
                    if ((x * x) + (y * y) + (z * z) < r_squared) {
                        returnvalues.add(new WorldPos(world, coords.getX() + x, coords.getY() + y, coords.getZ() + z));
                    }
                }
            }
        }
        return returnvalues;
    }

    public static HashSet<WorldPos> getShell(WorldPos center, int radius) {
        // Josiah: I wrote this so it's probably got holes...
        HashSet<WorldPos> bigSphere = getSphere(center, radius);
        HashSet<WorldPos> smallerSphere = getSphere(center, radius - 1);
        bigSphere.removeAll(smallerSphere);
        return bigSphere;
    }
}
