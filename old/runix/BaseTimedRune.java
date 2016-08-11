package com.newlinegaming.runix;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public abstract class BaseTimedRune extends PersistentRune {

//    RuneTimer instance = null;
    
    public BaseTimedRune(){}
    public BaseTimedRune(WorldPos coords, EntityPlayer player2, String name) {
        super(coords, player2, name);
        
    }

    /**
     * This registers the rune as being actively updated.  Forge (thru RuneTimer) will call
     * onUpdateTick() every xTicks from here on out until it is turned off.  There are
     * 20 ticks per second.
     * @param xTicks number of ticks to wait between calls.  20 ticks = 1 second
     */
    protected void updateEveryXTicks(int xTicks) {
	RuneTimer instance = new RuneTimer(this, xTicks);
    	MinecraftForge.EVENT_BUS.register(instance);
    }

    
    protected abstract void onUpdateTick(EntityPlayer player);

}
