package com.newlinegaming.runix.item;

import com.newlinegaming.runix.item.armor.ArmorAetherGoggles;
import com.newlinegaming.runix.lib.LibInfo;

import net.minecraft.item.Item;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItem {

    public static Item aetherGoggles;
    public static Item transRod;
    
    public static void init() {

        aetherGoggles = new ArmorAetherGoggles();
        transRod = new ItemTransmutationRod();
        
        gameReg();
    }

    private static void gameReg() {
        GameRegistry.registerItem(aetherGoggles, LibInfo.MOD_ID  + "aethergoggles");
        GameRegistry.registerItem(transRod, LibInfo.MOD_ID + "transmutationrod");
        
    }
}
