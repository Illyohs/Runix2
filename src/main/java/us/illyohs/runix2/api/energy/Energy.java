package us.illyohs.runix2.api.energy;

import net.minecraft.block.Block;

public class Energy
{

    Block   block;
    int     energyValue;
    boolean natural;
    boolean crushable;
    boolean sensitive;

    public Energy(Block block, int energyValue, boolean natural, boolean crushable, boolean sensitive)
    {
        this.block = block;
        this.energyValue = energyValue;
        this.natural = natural;
        this.crushable = crushable;
        this.sensitive = sensitive;
    }

    public Block getBlock()
    {
        return block;
    }

    public int getEnergyValue()
    {
        return energyValue;
    }

    public boolean isCrushable()
    {
        return crushable;
    }

    public boolean isNatural()
    {
        return natural;
    }

    public boolean isSensitive()
    {
        return sensitive;
    }
}
