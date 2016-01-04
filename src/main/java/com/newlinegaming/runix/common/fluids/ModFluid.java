package com.newlinegaming.runix.common.fluids;

import us.illyohs.libilly.block.fluid.FluidBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModFluid {

    public static Fluid     QixSilver;

    // public static Block BlockQixSilver;
    public static FluidBase qixSilver;

    public static void init() {

        // Fluids
        // QixSilver = new FluidQixSilver();

        // Fluid Blocks
//        BlockQixSilver = new BlockQixSilver(QixSilver, Material.lava);

        GameReg();
    }

    private static void GameReg() {

//        GameRegistry.registerBlock(BlockQixSilver, "runixqixsilver");
    }
}
