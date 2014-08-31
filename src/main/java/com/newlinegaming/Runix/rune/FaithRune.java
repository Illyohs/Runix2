package com.newlinegaming.Runix.rune;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import com.newlinegaming.Runix.NotEnoughRunicEnergyException;
import com.newlinegaming.Runix.PersistentRune;
import com.newlinegaming.Runix.Tiers;
import com.newlinegaming.Runix.block.ModBlock;
import com.newlinegaming.Runix.utils.Util_Movement;
import com.newlinegaming.Runix.utils.Util_SphericalFunctions;
import com.newlinegaming.Runix.WorldXYZ;

public class FaithRune extends PersistentRune{
	
	protected static ArrayList<PersistentRune> activeFaithList = new ArrayList<PersistentRune>();
	public Integer radius = 11;
    private boolean firstTime;
    protected boolean useCollisionDetection = true;//option to turn off collision detection through JSON
    private transient HashSet<WorldXYZ> sphere = null;//volatile so that JSON doesn't try to cache this thing
    
	public FaithRune() {
	    runeName = "Faith";
	}
	public FaithRune(WorldXYZ loc, EntityPlayer creator) {
	    super(loc, creator, "Faith");
	    firstTime = true;
	}
	
	public Block[][][] runicTemplateOriginal(){
	    Block gold = Blocks.gold_block;
		return new Block[][][] {{
		    {NONE,NONE,NONE},
		    {NONE,gold,NONE},
		    {NONE,NONE,NONE}},
		    {{TIER,gold,TIER},
		     {gold,TIER,gold},
		     {TIER,gold,TIER}
		}};
	}

	
    @Override
    protected void poke(EntityPlayer poker, WorldXYZ coords) {
        if(firstTime){// firstTime prevents players from injecting more energy by building a second rune on top of the first
            firstTime = false;
            consumeRune(coords);
            try {
                setBlockIdAndUpdate(coords, ModBlock.fakeGoldBlock); //Gold block is to be a permanent marker
            } catch (NotEnoughRunicEnergyException e) {}
            energy -= Tiers.getEnergy(Blocks.gold_block) * 5; //the Gold blocks don't count towards the energy
            radius = Tiers.energyToRadiusConversion(energy);
            HashSet<WorldXYZ> tsphere = fullStructure();
            energy -= tsphere.size() * Tiers.blockMoveCost;
            aetherSay(poker, "Created a Faith Sphere with a radius of "+ radius + " and " + sphere.size() + " blocks.");
            bounceIsland();
        }
    }
    
    /**
     * bouncIsland() will place the sphere sitting on top of the old sphere's location (y+diameter).  It is used the first time
     * Faith is activated. 
     * Josiah: I've tried to speed this up as much as possible with little effect.  Profiling is needed.
     * @param sphere coordinates passed in so they don't need to be recalculated
     */
    public void bounceIsland() {
        //assumes fullStructure() has already been called
        int height = Math.min(location.posY + radius*2+1, 255 - radius-1);// places a ceiling that does not allow islands to go out the top of the map
        if(location.posY + radius*2 <= height) {
            HashSet<WorldXYZ> structure = attachedStructureShape(getPlayer(), sphere);
            WorldXYZ destination = new WorldXYZ(location.posX, height, location.posZ); // scan up, starting at target height
            moveStructureAndPlayer(getPlayer(), destination, structure);//scan UP, 0 buffer room
        } else {
            aetherSay(getPlayer(), "Radius: " + radius +
                    ". There's not enough room left to bounce. This island can be moved with an FTP, provided there is enough room under build height at the destination.");
        }
	}
    
    @Override
    public HashSet<WorldXYZ> fullStructure() {
        if(sphere == null)
            sphere = Util_SphericalFunctions.getSphere(location, radius);
        return sphere;
    }
    
    @Override
    public HashSet<WorldXYZ> runeBlocks() {
        HashSet<WorldXYZ> st = new HashSet<WorldXYZ>();
        st.add(location);
        return st;
    }
    
    @Override
    public int authority() {  
        return radius;
    }
    
    //@Override
    /**moveMagic() On Faith Islands checks for a movement of the center block.  If that block (location) gets moved, then the move
     * is amplified to all other blocks in the radius of the Faith Island.  This will be a little odd with rotation moves.*
    public void moveMagic(HashMap<WorldXYZ, WorldXYZ> positionsMoved) 
    {
        for(PersistentRune rune : getActiveMagic())
        {
            if(positionsMoved.keySet().contains(rune.location) ) // we have a move that affects the center
            {
                try {
                    ((FaithRune) rune).moveIsland(positionsMoved);
                } catch (NotEnoughRunicEnergyException e) {
                    aetherSay(location.getWorld(), "The Faith sphere at " + location.toString() + " has run out of energy and the " +
                    		"magic has become untethered from the center block.");
                }
            }
        }
    }*/

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return activeFaithList;
    }

    @Override
    public boolean oneRunePerPerson() {
        return false;
    }
    @Override
    public boolean isFlatRuneOnly(){
        return true;
    }	

}
