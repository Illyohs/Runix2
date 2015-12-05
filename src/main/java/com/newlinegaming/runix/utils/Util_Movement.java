package com.newlinegaming.runix.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.newlinegaming.runix.*;
import com.newlinegaming.runix.handlers.RuneHandler;
import com.newlinegaming.runix.workers.StructureMoveWorker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class Util_Movement {

    public static HashMap<WorldPos, WorldPos> xzRotation(Collection<WorldPos> startingShape, WorldPos centerPoint, boolean counterClockwise){
        //centerPoint is also the axis of rotation
        HashMap<WorldPos, WorldPos> rotationMapping = new HashMap<WorldPos, WorldPos>();
        for( WorldPos point : startingShape ){
            rotationMapping.put(point, point.rotate(centerPoint, counterClockwise));//flip sign on X
        }
        return rotationMapping;
    }
    

    /**
     * Note: when designing moving runes, DO NOT update your PersistentRune.location variable.
     * moveShape() calls moveMagic() which will update everything including yourself.
     */
    public static HashSet<WorldPos> performMove(HashMap<WorldPos, WorldPos> moveMapping) {
        IBlockState AIR = Blocks.air.getDefaultState();
        HashMap<WorldPos, IBlockState> newStructure = new HashMap<WorldPos, IBlockState>();
        HashMap<WorldPos, IBlockState> sensitiveBlocks = new HashMap<WorldPos, IBlockState>();
        for(WorldPos point : moveMapping.keySet()) {
            IBlockState block = point.getState();
            //we're splitting sensitive blocks into their own set
            if (Tiers.isMoveSensitive(block.getBlock())) {
                //record at new location
                sensitiveBlocks.put(moveMapping.get(point), block);                
                //delete sensitive blocks first to prevent drops
                point.setBlockState(AIR); 
                //don't write AIR blocks
            } else if(block.getBlock() == Blocks.air) {
                //record original at new location
                newStructure.put(moveMapping.get(point), block);
            }
        }
        
        for(WorldPos loc : moveMapping.keySet()) //Delete everything
            loc.setBlockState(AIR); // delete old block in a separate loop to avoid collisions with the new positioning

        for(WorldPos destination : newStructure.keySet()) //place all the blocks at new location
//            destination.setBlockState(newStructure.get(destination));//doesn't include sensitive blocks
            destination.setBlockState(newStructure.get(destination));

        for(WorldPos specialPos : sensitiveBlocks.keySet()) //Place all the sensitive blocks
            specialPos.setBlockState(sensitiveBlocks.get(specialPos));//blocks like torches and redstone
        
        RuneHandler.getInstance().moveMagic(moveMapping);

        HashSet<WorldPos> newPositions = new HashSet<WorldPos>(newStructure.keySet());
        newPositions.addAll(sensitiveBlocks.keySet());//merge sensitive locations back in with normal
        return newPositions;
    }

    /**
     * Geometry: figure out if we're on the left or right side of the rune relative to the player
     */
    public static boolean lookingRightOfCenterBlock(EntityPlayer player, WorldPos referencePoint) {
        float yaw = player.rotationYawHead;//assumption: you're looking at the block you right clicked
        yaw = (yaw > 0.0) ? yaw  : yaw + 360.0F; //Josiah: minecraft yaw wanders into negatives sometimes...
        double opposite = player.posZ - referencePoint.getZ() - .5;
        double adjacent = player.posX - referencePoint.getX() - .5;
        double angle = Math.toDegrees(Math.atan( opposite / adjacent )) + 90.0;
        if( adjacent > 0.0)
            angle += 180.0;
//        System.out.println("Rune: " + angle + "  Yaw: " + yaw + " = " + (angle - yaw));
        if( ((angle - yaw) < 180.0 && (angle - yaw) > 0.0) || //the difference between the angle to the reference
                ((angle - yaw) < -180.0 && (angle - yaw) > -360.0) )//and the angle we're looking determines left/right
            return true;
        else
            return false;
    }

    public static HashMap<WorldPos, WorldPos> displaceShape(Collection<WorldPos> set, WorldPos startPoint, WorldPos destinationCenter) {
        HashMap<WorldPos, WorldPos> moveMapping = new HashMap<WorldPos, WorldPos>();
        Vector3 displacement = new Vector3(startPoint, destinationCenter);
        for(WorldPos point : set)
            moveMapping.put(point, point.offsetWorld(displacement, destinationCenter.getWorld()));
        return moveMapping;
    }

    public static boolean shapeCollides(HashMap<WorldPos, WorldPos> move) {
        for(WorldPos newPos : move.values()){
            if( !move.containsKey(newPos) && newPos.getBlock() != Blocks.air && !Tiers.isCrushable(newPos.getBlock()))
                return true;
        }
        return false;
    }

    /**
     * Attempt to teleport the structure to a non-colliding location at destination, scanning in the direction of destination.face.
     * @param structure
     * @param destination
     * @return center of destination teleport or null if the teleport was unsuccessful
     * @throws NotEnoughRunicEnergyException
     */
    public static WorldPos safelyTeleportStructure(HashSet<WorldPos> structure, WorldPos startPoint, WorldPos destination, int radius) {
        Vector3 roomForShip = Vector3.facing[destination.face].multiply(radius);// TODO get width/height/depth of structure + 1
        int collisionTries = 0;
        HashMap<WorldPos, WorldPos> moveMapping = null;
        WorldPos destinationCenter = null;
        do {
            Vector3 stepSize = Vector3.facing[destination.face].multiply(5);//try moving it over 5m
            destinationCenter = destination.offset(roomForShip).offsetWorld(stepSize.multiply(collisionTries), destination.getWorld() ); //base roomForShip + collisionTries iterations
            moveMapping = displaceShape(structure, startPoint, destinationCenter);
            collisionTries++;
        } while( shapeCollides( moveMapping ) && collisionTries < 20);
            
        if(collisionTries >= 20)
            return null; //the teleport did not work
//        performMove(moveMapping);
        StructureMoveWorker worker = new StructureMoveWorker(moveMapping);
        worker.scheduleWorkLoad();
        return destinationCenter;
    }


    public static HashMap<WorldPos, IBlockState> rotateStructureInMemory(HashMap<WorldPos, IBlockState> shape, WorldPos center, int nTurns) {
        HashMap<WorldPos, IBlockState> startShape = new HashMap<WorldPos, IBlockState>(shape);
        
        for(int turnNumber = 0; turnNumber < nTurns; ++turnNumber) {
            HashMap<WorldPos, WorldPos> move = Util_Movement.xzRotation(startShape.keySet(), center, false);

            HashMap<WorldPos, IBlockState> newShape = new HashMap<WorldPos, IBlockState>();//blank variable for swapping purposes
            for(WorldPos origin : move.keySet()) {
                WorldPos destination = move.get(origin);
                newShape.put(destination, startShape.get(origin));
            }
            startShape = newShape;
        }
        return startShape;
    }

    public static HashMap<WorldPos, IBlockState> scanBlocksInShape(Set<WorldPos> shape) {
        HashMap<WorldPos, IBlockState> actualBlocks = new HashMap<WorldPos, IBlockState>();
        for(WorldPos point : shape) {
            actualBlocks.put(point, point.getState());
        }
        return actualBlocks;
    }

}
