package com.newlinegaming.runix.common.item;

import com.newlinegaming.runix.common.item.armor.ArmorAetherGoggles;
import com.newlinegaming.runix.common.utils.UtilInfo;

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
        GameRegistry.registerItem(aetherGoggles, UtilInfo.MOD_ID  + "aethergoggles");
        GameRegistry.registerItem(transRod, UtilInfo.MOD_ID + "transmutationrod");
        
    }
}
