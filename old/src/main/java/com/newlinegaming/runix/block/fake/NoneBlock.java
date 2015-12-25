package com.newlinegaming.runix.block.fake;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;


public class NoneBlock extends Block {

    public NoneBlock() {
        super(Material.air);
        setUnlocalizedName("NONE");
    }
    
}