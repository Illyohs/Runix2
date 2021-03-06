package com.newlinegaming.runix;


import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RuneTimer {

    BaseTimedRune rune;
    private int currentTimer = 0;
    private int maxTimer = 20;

    RuneTimer(BaseTimedRune r, int waitTicks) {
        rune = r;
        currentTimer = 0;
        maxTimer = waitTicks;
    }
    
    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
    	++currentTimer;
    	if(currentTimer >= maxTimer) {
    		currentTimer = 0;
    		if(!rune.disabled) {
    		    rune.onUpdateTick(event.player);
    		}

    	}
    }
    
}