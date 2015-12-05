package com.newlinegaming.runix.block;

import com.newlinegaming.runix.tile.TileLightBeam;

import us.illyohs.libilly.block.AirBlockBase;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLightBeam extends AirBlockBase implements ITileEntityProvider {

    public BlockLightBeam(Material material, String name, float hardness, float resistance, float light, boolean tick, boolean isBlockAir, CreativeTabs tab) {
        super(material, name, hardness, resistance, light, tick, isBlockAir, tab);
        this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    // public BlockLightBeam() {
    // super(Material.air);
    // setCreativeTab(RunixMain.TabRunix);
    // setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    // setUnlocalizedName("LibInfo.MOD_ID" +":blank");
    // setLightLevel(17f);
    // setLightOpacity(10);
    // }

    // @Override
    // public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_,
    // int x, int y, int z) {
    // return null;
    // }
    //
    //
    // @Override
    // public boolean renderAsNormalBlock() {
    // return false;
    //
    // }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        // TODO Auto-generated method stub
        return new TileLightBeam();
    }

}
