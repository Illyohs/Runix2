package com.newlinegaming.runix.rune;

import java.util.ArrayList;

import com.newlinegaming.runix.NotEnoughRunicEnergyException;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.WorldPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class TeleporterRune extends PersistentRune {

    private static ArrayList<PersistentRune> energizedTeleporters = new ArrayList<PersistentRune>();
    
    public TeleporterRune(){
        super();
        runeName = "Teleporter";
    }
    
    public TeleporterRune(WorldPos coords, EntityPlayer activator){
        super(coords, activator,"Teleporter");
        energy = 1;
    }

	public Block[][][] runicTemplateOriginal(){
		return new Block[][][]
				{{{NONE,TIER,SIGR,TIER,NONE},
				  {TIER,TIER,TIER,TIER,TIER},
				  {SIGR,TIER,FUEL ,TIER,SIGR},
				  {TIER,TIER,TIER,TIER,TIER},
				  {NONE,TIER,SIGR,TIER,NONE}}};
	}
	

    /**
     * Teleport the player to the WaypointRune with a matching signature
     */
    @Override
    protected void poke(EntityPlayer poker, WorldPos coords) {
        consumeFuelBlock(coords);
	    WorldPos destination = findWaypointBySignature(poker, getSignature());

	    if(destination != null){
	        aetherSay(poker, "Teleporting to " + destination.toString());
    		try {
                teleportPlayer(poker, destination);
            } catch (NotEnoughRunicEnergyException e) {
                reportOutOfGas(poker);
            }
	    }
	}

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return energizedTeleporters;
    }

    public boolean oneRunePerPerson() {
        return false;
    }

    @Override
    public boolean isFlatRuneOnly() {
        return false;
    }

}
