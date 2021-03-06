package com.newlinegaming.runix.workers;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.newlinegaming.runix.WorldPos;
import net.minecraft.init.Blocks;

import com.newlinegaming.runix.Tiers;
import com.newlinegaming.runix.Vector3;
import com.newlinegaming.runix.handlers.RuneHandler;
import com.newlinegaming.runix.lib.LibConfig;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class StructureMoveWorker implements IBlockWorker {

    private HashMap<WorldPos, WorldPos> moveMapping = null;
    HashSet<WorldPos> newPositions = new HashSet<WorldPos>();
    HashMap<WorldPos, SigBlock> sensitiveBlocks = null;
    private WorldPos bumpedBlock = null;  // created whenever a move collides with itself
    private int currentTimer = 0;
    private int maxTimer = 20; // 20 ticks = 1 second
    private Iterator<Entry<WorldPos, WorldPos> > cursor = null;
    private boolean searchingForSensitive;
    
    public StructureMoveWorker(HashMap<WorldPos, WorldPos> moveMap){
        moveMapping = moveMap;
        cursor = moveMapping.entrySet().iterator();
        sensitiveBlocks = new HashMap<WorldPos, SigBlock>();
        searchingForSensitive = true;
        System.out.println("Starting the StructureMoveWorker on" + moveMapping.size());
    }
    
    @SubscribeEvent
    public void doWork(TickEvent.ServerTickEvent event) {
        ++currentTimer;
        if( !isFinished() && currentTimer >= maxTimer){ // called only once per second
            currentTimer = 0;
            SigBlock AIR = new SigBlock(Blocks.air, 0);
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
                        SigBlock block = move.getKey().getSigBlock();
                        while( Tiers.isMoveSensitive(block.blockID) ){//we're splitting sensitive blocks into their own set
                            ++sensitiveBlocksFound;
                            sensitiveBlocks.put(move.getValue(), block);//record at new location
                            move.getKey().setBlockId(AIR);//delete sensitive blocks first to prevent drops
                            //TODO there's a tiny probability of breaking an extended piston
                            //iterate upward for stacks of gravel, sand, stems, tall grass etc.
                            WorldPos up = move.getKey().offset(Vector3.UP);
                            block = up.getSigBlock();
                            if(!moveMapping.containsKey(up)){
                                break;
                            }
                            move = new AbstractMap.SimpleEntry<WorldPos, WorldPos>(up, moveMapping.get(up));  // add only after we know it's there
                        }
                        if( sensitiveBlocksFound > LibConfig.STRUCWORKER_DEFAULT){ //amount of change this tick //FIXME: config option
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
                        SigBlock block = move.getKey().getSigBlock();
                        if(block.equals(Blocks.air)) { 
                            airBlocks.put(move.getKey(), move.getValue()); //don't calculate on AIR blocks
                        } else {
                            currentMove.put(move.getKey(), move.getValue());
                        }
                        if( currentMove.size() + (airBlocks.size() / 5) > LibConfig.STRUCWORKER_DEFAULT) //FIXME: config option
                            break;
                    }
                    //we no longer need to delete things from moveMapping because the cursor keeps our spot
                    
                    //Step 3: placement
                        //take the next 100 blocks
                        //move those blocks
                    for(WorldPos origin : currentMove.keySet()) { //Do Move
                        WorldPos destination = currentMove.get(origin);
                        SigBlock block = origin.getSigBlock();
                        destination.setBlockId(block);  //set at destination
                        origin.setBlockId(AIR); //delete at origin
                        // TODO: delete old block in a separate loop to avoid collisions with the new positioning
                    }
                    
                    newPositions.addAll(currentMove.values());
                    RuneHandler.getInstance().moveMagic(currentMove);
                    RuneHandler.getInstance().moveMagic(airBlocks);
                    
                } else { //last phase
                  //Step 4: place sensitive blocks
//                    LogHelper.info("Placing sensitive blocks");
                    for(WorldPos specialPos : sensitiveBlocks.keySet()) {//Place all the sensitive blocks
                        specialPos.setBlockId(sensitiveBlocks.get(specialPos));//blocks like torches and redstone
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
        FMLCommonHandler.instance().bus().register(this);
    }

}
