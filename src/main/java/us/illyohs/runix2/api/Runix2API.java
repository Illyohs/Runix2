package us.illyohs.runix2.api;

import java.util.HashMap;

import net.minecraft.block.Block;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import us.illyohs.runix2.api.energy.Energy;
import us.illyohs.runix2.api.rune.Rune;

public class Runix2API
{

    private static HashMap<Block, Energy> energyRegistry = new HashMap<Block, Energy>();

    ///////////////////////////// Forge Registries /////////////////////////////
    public static IForgeRegistry<Rune>  RUNE = GameRegistry.findRegistry(Rune.class);




    public static void addEnergy(Block block, int value, boolean natural, boolean crushable, boolean sensitive)
    {
        energyRegistry.put(block, new Energy(block, value, natural, crushable, sensitive));
    }

    public static int getEnergyFromBlock(Block block)
    {
        return energyRegistry.get(block).getEnergyValue();
    }

    public static boolean isNatural(Block block)
    {
        if (!energyRegistry.containsKey(block))
        {
            return false;
        }

        return energyRegistry.get(block).isNatural();
    }

    public static boolean isMoveSensitive(Block block)
    {
        if(!energyRegistry.containsKey(block))
        {
            return false;
        }

        return energyRegistry.get(block).isSensitive();
    }

    public static boolean isCrushable(Block block)
    {
        if (!energyRegistry.containsKey(block))
        {
            return false;
        }

        return energyRegistry.get(block).isCrushable();
    }
}
