package com.newlinegaming.runix.api;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class RunixApi {
    
    public static ArmorMaterial armorRunix = EnumHelper.addArmorMaterial("RUNIXIUMARMOR", "runix", 30, new int[]{4, 6, 6, 4}, 25);
}
