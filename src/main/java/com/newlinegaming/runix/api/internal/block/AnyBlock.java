package com.newlinegaming.runix.api.internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class AnyBlock extends Block {

    public AnyBlock() {
        super(Material.air);
        setUnlocalizedName("ANY");
    }

}
