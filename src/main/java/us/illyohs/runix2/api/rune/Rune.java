package us.illyohs.runix2.api.rune;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import us.illyohs.runix2.api.impl.FakeRuneBlock.FuelBlock;
import us.illyohs.runix2.api.impl.FakeRuneBlock.NullBlock;
import us.illyohs.runix2.api.impl.FakeRuneBlock.KeyBlock;
import us.illyohs.runix2.api.impl.FakeRuneBlock.TierBlock;

public abstract class Rune extends IForgeRegistryEntry.Impl<Rune>
{

    String  unLocalizedName;
    boolean isConsumable;
    boolean isFlat;


    public static final Block KEY  = new KeyBlock();
    public static final Block FULE = new FuelBlock();
    public static final Block NULL = new NullBlock();
    public static final Block TIER = new TierBlock();

    abstract public Block[][][] pattern();

    abstract public void execute(World world, BlockPos pos, EntityPlayer player);

    public void setFlat(boolean flat)
    {
        isFlat = flat;
    }

    public boolean isFlat()
    {
        return isFlat;
    }

    public void setConsumable(boolean consumable)
    {
        isConsumable = consumable;
    }

    public boolean isConsumable()
    {
        return isConsumable;
    }

    public String getUnLocalizedName()
    {
        return unLocalizedName;
    }

    public void setUnLocalizedName(String unLocalizedName)
    {
        this.unLocalizedName = unLocalizedName;
    }
}
