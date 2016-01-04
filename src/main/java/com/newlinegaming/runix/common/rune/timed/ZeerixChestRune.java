package com.newlinegaming.runix.common.rune.timed;

import java.util.ArrayList;
import java.util.HashSet;

import com.newlinegaming.runix.api.internal.NotEnoughRunicEnergyException;
import com.newlinegaming.runix.api.math.Vector3;
import com.newlinegaming.runix.api.math.WorldPos;
import com.newlinegaming.runix.common.rune.AbstractTimedRune;
import com.newlinegaming.runix.common.rune.PersistentRune;
import com.newlinegaming.runix.common.utils.Util_SphericalFunctions;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ZeerixChestRune extends AbstractTimedRune {
    protected static ArrayList<PersistentRune> activeMagic = new ArrayList<PersistentRune>();
    
    public ZeerixChestRune() {
        runeName = "Zeerix Chest";
        updateEveryXTicks(200);
    }

    public ZeerixChestRune(WorldPos coords, EntityPlayer player2) {
        super(coords, player2, "Zeerix Chest");
        updateEveryXTicks(200);
    }

    @Override
    protected void onUpdateTick(EntityPlayer subject) {
        if(subject.equals(getPlayer()))
        {
            double distance = (new WorldPos(getPlayer())).getDistance(location);//distance from player to current chest
            if( distance > 6.0){
                HashSet<WorldPos> sphere = Util_SphericalFunctions.getShell(new WorldPos(getPlayer()), 4);
                for(WorldPos newPos : sphere)
                {
                    if(newPos.getBlock() == Blocks.air 
                            && newPos.offset(Vector3.DOWN).isSolid()// base is solid 
                            && !newPos.offset(Vector3.UP).isSolid()){//room to open lid
                        try{
                            if(location.getBlock() != Blocks.ender_chest)
                                setBlockIdAndUpdate(location, Blocks.ender_chest);//charge for a replacement
//                                setBlockIdAndUpdate(coords, sourceBlock);
                            moveBlock(location, newPos);
                        }catch( NotEnoughRunicEnergyException e){
                            reportOutOfGas(getPlayer());
                        }
                        return; //we only need place the chest in one good position
                    }
                }
            } //else do nothing
        }
    }
  
    @Override
    public Block[][][] runicTemplateOriginal() {
        Block GOLD = Blocks.gold_ore;
        Block CHEST = Blocks.ender_chest;
        Block WOOD = Blocks.planks;
        return new Block[][][] {{
            {GOLD, NONE, GOLD},
            {NONE, CHEST, NONE},
            {GOLD, NONE, GOLD}},
            {{WOOD,TIER, WOOD},
            {TIER,TIER, TIER},
            {WOOD,TIER, WOOD}
            
        }};
    }

    @Override
    public String getRuneName() {
        return "Zeerix Chest";
    }

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return activeMagic;
    }

    @Override
    public boolean oneRunePerPerson() {
        return true;
    }
    
    public boolean isFlatRuneOnly() {
        return true;
    }
}
