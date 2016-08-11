package us.illyohs.runix2.api.impl;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class FakeRuneBlock extends Block
{
    public FakeRuneBlock(String name)
    {
        super(Material.AIR);
        this.setUnlocalizedName(name);
    }

    public static class AnyBlock extends FakeRuneBlock
    {
        public AnyBlock()
        {
            super("ANY");
        }
    }

    public static class KeyBlock extends FakeRuneBlock
    {
        public KeyBlock()
        {
            super("KEY");
        }
    }

    public static class FuelBlock extends FakeRuneBlock
    {

        public FuelBlock()
        {
            super("FUEL");
        }
    }

    public static class NullBlock extends FakeRuneBlock
    {
        public NullBlock()
        {
            super("NONE");
        }
    }

    public static class TierBlock extends FakeRuneBlock
    {
        public TierBlock()
        {
            super("TIER");
        }
    }
}
