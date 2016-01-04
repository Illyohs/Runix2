package com.newlinegaming.runix.common.rune.normal;

import java.util.ArrayList;

import com.newlinegaming.runix.api.Energy;
import com.newlinegaming.runix.api.math.WorldPos;
import com.newlinegaming.runix.common.handlers.RuneHandler;
import com.newlinegaming.runix.common.rune.AbstractRune;
import com.newlinegaming.runix.common.rune.PersistentRune;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class OracleRune extends AbstractRune {
    
    public OracleRune() {
        runeName = ("Oracle Rune");
    }

    @Override
    protected Block[][][] runicTemplateOriginal() {
        
        Block RED = Blocks.redstone_wire;
        
        return new Block[][][] {{
            {RED, RED ,RED},
            {RED, TIER, RED},
            {RED, RED, RED}
            
        }};
    }

    @Override
    public boolean isFlatRuneOnly() {
        return true;
    }

    @Override
    public void execute(WorldPos coords, EntityPlayer player) {
      ItemStack toolUsed = player.getHeldItem();
      
      if(toolUsed !=null && toolUsed.getItem() == Items.golden_sword || 
              toolUsed !=null && toolUsed.getItem() == Items.stone_sword || 
              toolUsed !=null && toolUsed.getItem() == Items.wooden_sword ||
              toolUsed !=null && toolUsed.getItem() == Items.diamond_sword) {
          
          ArrayList<PersistentRune> d = RuneHandler.getInstance().getAllRunesByPlayer(player);
          aetherSay(player, "Current enchantments: " + Integer.toString(d.size()));
          for (PersistentRune r : d) {
              aetherSay(player, r.runeName + " Energy: "+ r.energy);
          }
          
      } else {
          
          Block block = coords.getBlock();
          
          aetherSay(player, EnumChatFormatting.RED +block.getLocalizedName());
          aetherSay(player, "Tier: "  + Energy.getTier(block) + ".");
          aetherSay(player, "Energy: " + Energy.getEnergy(block) + ".");
          aetherSay(player, "Properties: " + (Energy.isNatural(block)? "Not Conductive" : "Conductive")
                  + ", " + (Energy.isCrushable(block)? "Crushable." : "Not Crushable."));
      }
      
    }
}
