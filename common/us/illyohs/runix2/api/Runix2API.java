package us.illyohs.runix2.api;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Created by illyohs on 12/25/15.
 */
public class Runix2API {

    //TOOLS & Materials
    public static ArmorMaterial armorRunix = EnumHelper.addArmorMaterial("RUNIXIUMARMOR", "runix", 30, new int[]{4, 6, 6, 4}, 25);

}
