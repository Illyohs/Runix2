package com.newlinegaming.runix.block;


import com.newlinegaming.runix.block.fake.FakeBlock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlock {

    public static GreekFire greekFire;
    public static HoarFrost hoar_frost;
    public static Block lightBeam;
    
    //Fake/replacement Blocks
    public static Block fakeGoldBlock;
    public static Block runixAir;
    
    public static void init() {

        hoar_frost = new HoarFrost();
    	
//        lightBeam = new BlockLightBeam();
        fakeGoldBlock = new FakeBlock(Blocks.gold_block);
        runixAir = new RunixAirBlock();

        Gamereg();
    }

    private static void Gamereg() {
        GameRegistry.registerBlock(greekFire, "GreekFire");
        GameRegistry.registerBlock(hoar_frost, HoarFrostItem.class, "HoarFrost");
        GameRegistry.registerBlock(runixAir, "Fake_Air");
        GameRegistry.registerBlock(lightBeam, "RunixLightBeam");
        GameRegistry.registerBlock(fakeGoldBlock, "RunixFakeGoldBlock");
        
        
    }
}
