package com.newlinegaming.Runix.runes;

import java.util.ArrayList;

import com.newlinegaming.Runix.NotEnoughRunicEnergyException;
import com.newlinegaming.Runix.PersistentRune;
import com.newlinegaming.Runix.WorldXYZ;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class FerrousWheelRune extends PersistentRune {

    private static ArrayList<PersistentRune> globalWheel  = new ArrayList<PersistentRune>();
    public ArrayList<String> guestList = new ArrayList<String>();
    
    public FerrousWheelRune() {
        runeName = "Ferrous Wheel";
    }

    public FerrousWheelRune(WorldXYZ coords, EntityPlayer activator) {
        super(coords, activator, "Ferrous Wheel");
    }


    @Override
    public void execute(WorldXYZ coords, EntityPlayer activator) {
        if(!activator.worldObj.isRemote){//server only
            super.execute(coords, activator);
        }
            
    }

    
    @Override
    protected void poke(EntityPlayer poker, WorldXYZ coords) {
        if(poker.worldObj.isRemote)
            return;
        consumeKeyBlock(coords);
        if( !guestList.contains(poker.username) )
            guestList.add(poker.username);
        FerrousWheelRune next = getNextWheel(poker);
        if(next == null || next == this){
            aetherSay(poker, "Create more of these runes to teleport between them.");
            return;
        }
        try {
            teleportPlayer(poker, next.location);
        } catch (NotEnoughRunicEnergyException e) {
            aetherSay(poker, "place a valuable block in the middle and activate this again.");
        }
    }

    public FerrousWheelRune getNextWheel(EntityPlayer user){
        if(globalWheel.size() < 2)
            return null;
        int start = globalWheel.indexOf(this);
        for(int i = (start + 1) % globalWheel.size(); i != start; i = (i+1) % globalWheel.size()){
            FerrousWheelRune fw = (FerrousWheelRune)globalWheel.get(i);
            if( !fw.location.equals(location) && fw.guestList.contains(user.username) ) 
                return fw;
        }
        return null;
    }
    
    public String toString(){
        return hashCode() + ": @" + location.toString() + " guests: " + guestList;
    }
    
    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return globalWheel;
    }

    @Override
    public boolean oneRunePerPerson() {
        return false;
    }
    
    public boolean isFlatRuneOnly() {
        return false;
    }
    
    @Override
    public int[][][] runicTemplateOriginal() {
        int IRON = Block.oreIron.blockID;
        return new int[][][]
                {{{IRON,IRON,IRON},
                  
                  {IRON,KEY ,IRON},
                  
                  {IRON,IRON,IRON}}};
    }

    @Override
    public String getRuneName() {
        return "Ferrous Wheel";
    }

}
