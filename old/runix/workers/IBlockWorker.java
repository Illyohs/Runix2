package com.newlinegaming.runix.workers;


import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public interface IBlockWorker {
    
    public void doWork(ServerTickEvent event);
    
    public boolean isFinished();
    
    public void scheduleWorkLoad();
    
//    void render();
    
}
