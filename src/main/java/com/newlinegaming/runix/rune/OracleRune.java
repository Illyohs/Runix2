package com.newlinegaming.runix.rune;

import java.util.ArrayList;

import com.newlinegaming.runix.BaseRune;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.handlers.RuneHandler;
import com.newlinegaming.runix.Tiers;
import com.newlinegaming.runix.WorldPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class OracleRune extends BaseRune {
    
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
          aetherSay(player, "Tier: "  + Tiers.getTier(block) + ".");
          aetherSay(player, "Energy: " + Tiers.getEnergy(block) + ".");
          aetherSay(player, "Properties: " + (Tiers.isNatural(block)? "Not Conductive" : "Conductive")
                  + ", " + (Tiers.isCrushable(block)? "Crushable." : "Not Crushable."));
      }
      
    }
}
