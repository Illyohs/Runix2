package com.newlinegaming.runix.rune;

import java.util.ArrayList;
import java.util.HashSet;

import com.newlinegaming.runix.AbstractTimedRune;
import com.newlinegaming.runix.NotEnoughRunicEnergyException;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.Vector3;
import com.newlinegaming.runix.WorldPos;
import com.newlinegaming.runix.utils.Util_SphericalFunctions;

import us.illyohs.libilly.util.BlockUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/**
 * TorchBearer functionality to place permanent torches appropriately spaced to prevent monster spawn.
 */
public class TorchBearerRune extends AbstractTimedRune {
    protected static ArrayList<PersistentRune> activeMagic = new ArrayList<PersistentRune>();
    public TorchBearerRune() {
        runeName = "Torch Bearer";
        updateEveryXTicks(10);
    }

    public TorchBearerRune(WorldPos coords, EntityPlayer activator ) {
        super(coords, activator, "Torch Bearer");
        updateEveryXTicks(10);
    }

    @Override
    protected void onUpdateTick(EntityPlayer subject) {
    	if(subject.equals(getPlayer()) && !subject.worldObj.isRemote) {
            World world = subject.worldObj;//sphere can be optimized to donut
            location = new WorldPos(getPlayer());
            HashSet<WorldPos> sphere = Util_SphericalFunctions.getShell(location, 1);
            for(WorldPos newPos : sphere) {
                if(newPos.getBlock().equals(Blocks.air) && 
                	newPos.offset(Vector3.DOWN).getBlock().canPlaceTorchOnTop(world, new BlockPos(newPos.getX(), newPos.getY()-1, newPos.getZ())) && (
                        (world.isDaytime() && world.getLight(new BlockPos(newPos.getX(), newPos.getY()-1, newPos.getZ())) < 4) ||//day time checking == caves
                        (!world.isDaytime() && world.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(newPos.getX(), newPos.getY(), newPos.getZ())) < 4) ))//adjustable
                { 
                    //TODO: throw exception
//                    try {
//                        setBlockIdAndUpdate(newPos, Blocks.torch);//set torch
//                        setBlockIdAndUpdate(coords, Block);CV
                        BlockUtils.replaceBlock(newPos.getWorld(), newPos, Blocks.air, Blocks.torch);
//                        aetherSay(subject, Integer.toString(world.getBlockLightValue(newPos.posX, newPos.posY, newPos.posZ))+ 
//                                " light level.  Placing at " + (new Vector3(newPos, location).toString()));
//                    } catch (NotEnoughRunicEnergyException e) {
                        reportOutOfGas(getPlayer());
//                    }
                    return; //Light levels don't update til the end of the tick, so we need to exit
                }
            }
        }
    }

    @Override
    public Block[][][] runicTemplateOriginal() {
        Block TRCH = Blocks.torch;
        return new Block[][][] {{
        	{TIER,TRCH,TIER},
        	{TRCH,FUEL ,TRCH},
        	{TIER,TRCH,TIER}
        	
        }}; 
    }

    @Override
    public String getRuneName() {
        return "Torch Bearer";
    }

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return activeMagic;
    }

    @Override
    public boolean oneRunePerPerson() {
        return true;
    }

    @Override
    public boolean isFlatRuneOnly() {
        return true;
    }

}
