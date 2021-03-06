package com.newlinegaming.runix.rune;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.Vector3;
import com.newlinegaming.runix.WorldPos;

public class WaypointRune extends PersistentRune{
    private static ArrayList<PersistentRune> activeMagic = new ArrayList<PersistentRune>();
    public int tier = 0;

    public WaypointRune(){
    	
    	super(); 
    	this.runeName = "Waypoint";
    }
    
    public WaypointRune(WorldPos coords, EntityPlayer player)
    {
    	super(coords, player, "Waypoint");
        
    }

    @Override
    public Block[][][] runicTemplateOriginal() {
        return new Block[][][]
                {{{NONE,TIER,TIER,TIER,NONE},
                  {TIER,TIER,SIGR,TIER,TIER},
                  {TIER,SIGR,TIER,SIGR,TIER},
                  {TIER,TIER,SIGR,TIER,TIER},
                  {NONE,TIER,TIER,TIER,NONE}}};
    }

    @Override
    /**Waypoints will detect which side of the key block you activate from and use 
     * that to direct the player's teleport.*/
    protected void poke(EntityPlayer poker, WorldPos coords) {
        location.face = coords.face; //update the facing of the waypoint
        aetherSay(poker, "Waypoint is now facing " + Vector3.faceString[location.face]);
    }

    public String getRuneName() {
		return this.runeName;
	}

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return activeMagic;
    }

    @Override
    public boolean oneRunePerPerson() {
        return false;
    }

    public boolean isFlatRuneOnly() {
        return false;
    }
    
}
