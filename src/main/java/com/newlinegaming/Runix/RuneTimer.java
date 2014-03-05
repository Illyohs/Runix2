package com.newlinegaming.Runix;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class RuneTimer implements ITickHandler
{
    AbstractTimedRune rune;
    private int currentTimer;
    private int maxTimer = 20;
    RuneTimer(AbstractTimedRune r, int waitTicks){
        rune = r;
        currentTimer = 0;
        maxTimer = waitTicks;
    }
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.PLAYER))) {
            ++currentTimer;
            if( currentTimer >= maxTimer)
            {
                currentTimer = 0;
                if(rune.disabled == false)
                    rune.onUpdateTick((EntityPlayer) tickData[0]);
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER, TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return null;
    }
}