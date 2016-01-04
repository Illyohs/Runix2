package com.newlinegaming.runix.common.rune.normal;

import java.util.HashMap;

import com.newlinegaming.runix.api.math.WorldPos;
import com.newlinegaming.runix.common.rune.AbstractRune;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class CompassRune extends AbstractRune {

    public CompassRune() {
        runeName = "Compass";
    }

    public Block[][][] runicTemplateOriginal() {
        Block air = Blocks.air;// This is AIR 0 on purpose
        return new Block[][][] {{
                { TIER, air, air, air, TIER }, 
                { air, TIER, air, TIER, air }, 
                { air, air, TIER, air, air }, 
                { air, TIER, air, TIER, air }, 
                { TIER, air, air, air, TIER } 
                } };
    }

    public void execute(WorldPos coords, EntityPlayer player) {
        Block ink = getTierInkBlock(coords);
        Block air = Blocks.air;
        Block[][][] compassOutcome = new Block[][][] { { { air, ink, air }, { ink, air, ink }, { ink, air, ink } } };
        coords = coords.copyWithNewFacing(1);
        HashMap<WorldPos, IBlockState> stamp = patternToShape(compassOutcome, coords);
        if (stampBlockPattern(stamp, player))
            accept(player);
    }

    public String getRuneName() {
        return "Compass";
    }

    public boolean isFlatRuneOnly() {
        return true;
    }

}
