package com.newlinegaming.runix.common.workers;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.newlinegaming.runix.api.Energy;
import com.newlinegaming.runix.api.math.Vector3;
import com.newlinegaming.runix.api.math.WorldPos;
import com.newlinegaming.runix.common.handlers.RuneHandler;
import com.newlinegaming.runix.common.utils.UtilConfig;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class StructureMoveWorker implements IBlockWorker {

    private HashMap<WorldPos, WorldPos> moveMapping = null;
    HashSet<WorldPos> newPositions = new HashSet<WorldPos>();
    HashMap<WorldPos, IBlockState> sensitiveBlocks = null;
    private WorldPos bumpedBlock = null;  // created whenever a move collides with itself
    private int currentTimer = 0;
    private int maxTimer = 20; // 20 ticks = 1 second
    private Iterator<Entry<WorldPos, WorldPos> > cursor = null;
    private boolean searchingForSensitive;
    
    public StructureMoveWorker(HashMap<WorldPos, WorldPos> moveMap){
        moveMapping = moveMap;
        cursor = moveMapping.entrySet().iterator();
        sensitiveBlocks = new HashMap<WorldPos, IBlockState>();
        searchingForSensitive = true;
        System.out.println("Starting the StructureMoveWorker on" + moveMapping.size());
    }
    
    @SubscribeEvent
    public void doWork(ServerTickEvent event) {
        ++currentTimer;
        if( !isFinished() && currentTimer >= maxTimer){ // called only once per second
            currentTimer = 0;
            IBlockState AIR = Blocks.air.getDefaultState();
            //Step 1: collision
                //inside safelyTeleportStructure()
            //Step 2: remove sensitive everything
            if( searchingForSensitive) {
                if( !cursor.hasNext()) {//finished sensitive phase
                    searchingForSensitive = false;
                    cursor = moveMapping.entrySet().iterator(); //reset cursor to beginning for main move phase
                } else {
//                    LogHelper.info("Processing Sensitive blocks");
                    int sensitiveBlocksFound = 0; //necessary to track the amount of change
                    while(cursor.hasNext()){
                        Entry<WorldPos, WorldPos> move = cursor.next();
                        IBlockState state = move.getKey().getState();
                        while (Energy.isMoveSensitive(state.getBlock())){//we're splitting sensitive blocks into their own set
                            ++sensitiveBlocksFound;
                            sensitiveBlocks.put(move.getValue(), state);//record at new location
                            move.getKey().setBlockState(AIR);//delete sensitive blocks first to prevent drops
                            //TODO there's a tiny probability of breaking an extended piston
                            //iterate upward for stacks of gravel, sand, stems, tall grass etc.
                            WorldPos up = move.getKey().offset(Vector3.UP);
                            state = up.getState();
                            if(!moveMapping.containsKey(up)){
                                break;
                            }
                            move = new AbstractMap.SimpleEntry<WorldPos, WorldPos>(up, moveMapping.get(up));  // add only after we know it's there
                        }
                        if( sensitiveBlocksFound > UtilConfig.STRUCWORKER_DEFAULT){ //amount of change this tick //FIXME: config option
                            break;
                        }
                    }
                }
            } else { 
                if( cursor.hasNext()) { // do iterative work here
//                    LogHelper.info("Moving blocks");
                    HashMap<WorldPos, WorldPos> currentMove = new HashMap<WorldPos, WorldPos>();
                    HashMap<WorldPos, WorldPos> airBlocks = new HashMap<WorldPos, WorldPos>();
                    while(cursor.hasNext()){
                        Entry<WorldPos, WorldPos> move = cursor.next();
                        IBlockState block = move.getKey().getState();
                        if(block.equals(Blocks.air)) { 
                            airBlocks.put(move.getKey(), move.getValue()); //don't calculate on AIR blocks
                        } else {
                            currentMove.put(move.getKey(), move.getValue());
                        }
                        if( currentMove.size() + (airBlocks.size() / 5) > UtilConfig.STRUCWORKER_DEFAULT) //FIXME: config option
                            break;
                    }
                    //we no longer need to delete things from moveMapping because the cursor keeps our spot
                    
                    //Step 3: placement
                        //take the next 100 blocks
                        //move those blocks
                    for(WorldPos origin : currentMove.keySet()) { //Do Move
                        WorldPos destination = currentMove.get(origin);
                        IBlockState block = origin.getState();
                        destination.setBlockState(block);  //set at destination
                        origin.setBlockState(AIR); //delete at origin
                        // TODO: delete old block in a separate loop to avoid collisions with the new positioning
                    }
                    
                    newPositions.addAll(currentMove.values());
                    RuneHandler.getInstance().moveMagic(currentMove);
                    RuneHandler.getInstance().moveMagic(airBlocks);
                    
                } else { //last phase
                  //Step 4: place sensitive blocks
//                    LogHelper.info("Placing sensitive blocks");
                    for(WorldPos specialPos : sensitiveBlocks.keySet()) {//Place all the sensitive blocks
                        specialPos.setBlockState(sensitiveBlocks.get(specialPos));//blocks like torches and redstone
                    }
                    newPositions.addAll(sensitiveBlocks.keySet());//merge sensitive locations back in with normal
                    sensitiveBlocks.clear();
                    moveMapping.clear();
                    //return newPositions; //TODO: For Faith and FTP, return doesn't matter, it does matter for Runecraft
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return moveMapping.isEmpty()  && (sensitiveBlocks != null && sensitiveBlocks.isEmpty());
    }

    @Override
    public void scheduleWorkLoad() {
        MinecraftForge.EVENT_BUS.register(this);
    }

}
