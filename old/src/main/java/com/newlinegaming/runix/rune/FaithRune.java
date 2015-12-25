package com.newlinegaming.runix.rune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.newlinegaming.runix.NotEnoughRunicEnergyException;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.Tiers;
import com.newlinegaming.runix.WorldPos;
import com.newlinegaming.runix.block.ModBlock;
import com.newlinegaming.runix.utils.Util_Movement;
import com.newlinegaming.runix.utils.Util_SphericalFunctions;

import us.illyohs.libilly.util.BlockUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class FaithRune extends PersistentRune{
	
	protected static ArrayList<PersistentRune> activeFaithList = new ArrayList<PersistentRune>();
	public Integer radius = 11;
    private boolean firstTime;
    protected boolean useCollisionDetection = true;//option to turn off collision detection through JSON
    private transient HashSet<WorldPos> sphere = null;//volatile so that JSON doesn't try to cache this thing
    
	public FaithRune() {
	    runeName = "Faith";
	}
	public FaithRune(WorldPos loc, EntityPlayer creator) {
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
    protected void poke(EntityPlayer poker, WorldPos coords) {
        if(firstTime){// firstTime prevents players from injecting more energy by building a second rune on top of the first
            firstTime = false;
            consumeRune(coords);
            //                setBlockIdAndUpdate(coords, coords.se); //Gold block is to be a permanent marker
            BlockUtils.replaceBlock(coords.getWorld(), coords, coords.getBlock(), ModBlock.fakeGoldBlock);
            energy -= Tiers.getEnergy(Blocks.gold_block) * 5; //the Gold blocks don't count towards the energy
            radius = Tiers.energyToRadiusConversion(energy);
            HashSet<WorldPos> tsphere = fullStructure();
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
        int height = Math.min(location.getY() + radius*2+1, 255 - radius-1);// places a ceiling that does not allow islands to go out the top of the map
        if(location.getY() + radius*2 <= height) {
            HashSet<WorldPos> structure = attachedStructureShape(getPlayer(), sphere);
            WorldPos destination = new WorldPos(location.getX(), height, location.getZ()); // scan up, starting at target height
            moveStructureAndPlayer(getPlayer(), destination, structure);//scan UP, 0 buffer room
        } else {
            aetherSay(getPlayer(), "Radius: " + radius +
                    ". There's not enough room left to bounce. This island can be moved with an FTP, provided there is enough room under build height at the destination.");
        }
	}
    
    @Override
    public HashSet<WorldPos> fullStructure() {
        if(sphere == null)
            sphere = Util_SphericalFunctions.getSphere(location, radius);
        return sphere;
    }
    
    @Override
    /** This override is necessary to invalidate the buffered sphere variable whenever it is moved**/
    public WorldPos moveYourLocation(WorldPos destination) {
        sphere = null;
        location = destination.copyWithNewFacing(location.face); //preserve old facing
        return location;
    }    
    
    @Override
    public HashSet<WorldPos> runeBlocks(WorldPos coords) {
        HashSet<WorldPos> st = new HashSet<WorldPos>();
        st.add(location);
        return st;
    }
    
    @Override
    public int authority() {  
        return radius;
    }
    
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
