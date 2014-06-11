package com.newlinegaming.Runix;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class Tiers {

    //Note from LordIllyohs: Don't like this class I feel it would be better to add energy by doing something like RunicEnergy.addBlockEnergy(fooBlock, 56) maybe add it to a Runix
    //library mod?
    
    //Cost category values from the Spreadsheet
    //https://docs.google.com/spreadsheet/ccc?key=0AjI7rA2yIcubdG1XbTkxcTg5ZlJkSU1UU3NjOGhnQ0E&usp=drive_web#gid=0
    public static final float blockMoveCost = 1.0f;
    public static final int blockBreakCost = 12;
    public static final float movementPerMeterCost = 0.22f;
    
    private static ArrayList<String> naturalBlocks;
    private static ArrayList<String> moveSensitiveBlocks;
    private static ArrayList<String> crushableBlocks;
    private static int[] blockEnergy;
    
    public Tiers(){
        /**
         * naturalBlocks is an important list because it lists all blocks that will not conduct runic energy
         */
        Block[] extraNaturalBlocks = new Block[] {
                Blocks.water, Blocks.flowing_water, Blocks.bedrock,
                Blocks.sand, Blocks.stone, Blocks.dirt,
                Blocks.grass, Blocks.tallgrass, Blocks.snow,
                Blocks.mycelium, Blocks.netherrack,
                Blocks.lava, Blocks.flowing_lava, Blocks.vine, Blocks.leaves,
                Blocks.cactus, Blocks.deadbush,
                Blocks.ice, Blocks.sapling, Blocks.log};

        naturalBlocks = loadBlockIds(extraNaturalBlocks);
        naturalBlocks.add("air");// AIR 0 needs to be added manually
//        naturalBlocks.add(GreekFire.blockIdBackup);
        
        Block[] attachedOrFallingBlocks = new Block[] {
                Blocks.anvil, Blocks.cocoa, Blocks.carrots, Blocks.carpet, Blocks.wheat,
                Blocks.potatoes, Blocks.portal, Blocks.end_portal, Blocks.brewing_stand,
                Blocks.cactus, Blocks.deadbush, Blocks.dragon_egg, Blocks.fire,
                Blocks.grass, Blocks.gravel, Blocks.lava, Blocks.flowing_lava,
                Blocks.ladder, Blocks.leaves, Blocks.lever, Blocks.melon_stem,
                Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.nether_wart,
                Blocks.piston, //these ones may be co-dependent :?
                Blocks.piston_extension, Blocks.piston_head,
                Blocks.sticky_piston,
                Blocks.red_flower, Blocks.yellow_flower, Blocks.heavy_weighted_pressure_plate,
                Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.pumpkin,
                Blocks.rail, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail,
                Blocks.powered_comparator, Blocks.unpowered_comparator,
                Blocks.unpowered_repeater, Blocks.powered_repeater,
                Blocks.redstone_wire, Blocks.reeds, Blocks.sand, Blocks.sapling,
                Blocks.standing_sign, Blocks.wall_sign, Blocks.skull, Blocks.stone_button,
                Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook,
                Blocks.torch, Blocks.redstone_torch, Blocks.unlit_redstone_torch,
                Blocks.vine, Blocks.waterlily,
                Blocks.water, Blocks.flowing_water,
                Blocks.wooden_button};
        moveSensitiveBlocks = loadBlockIds(attachedOrFallingBlocks);
        
        Block[] crushTheseBlocks = new Block[]{
                Blocks.deadbush, Blocks.snow, Blocks.fire, Blocks.gravel, Blocks.water,
                Blocks.flowing_water, Blocks.sapling, Blocks.tallgrass, Blocks.torch,//torches are debatable, since someone did place it there
                Blocks.vine};
        crushableBlocks = loadBlockIds(crushTheseBlocks);
//        crushableBlocks.add(GreekFire.blockIdBackup);
        
        blockEnergy = new int[]{ //the blockID is the index for this array.  The value at blockEnergy[blockID] = runic energy
                1,   //Air
                1,   //Smooth Stone
                1,   //Grass
                1,   //Dirt
                1,   //Cobblestone
                8,   //Wooden Plank
                32,  //Sapling
                1,   //Bedrock
                16,  //Water
                16,  //Stationary Water
                80,  //Lava
                80,  //Stationary Lava
                5,   //Sand
                31,  //Gravel
                1519,    //Gold Ore
                158,     //Iron Ore
                84,  //Coal Ore
                32,  //Wood Log
                1,   //Leaves
                256,     //Sponge
                10,  //Glass
                3964,    //Lapis Lazuli Ore
                5946,    //Lapis Lazuli Block
                352,     //Dispenser
                20,  //Sandstone
                210,     //Note Block
                96,  //Bed
                1543,    //Powered Rail
                59,  //Detector Rail
                304,     //Sticky Piston
                3038,    //Cobweb
                1,   //Tall Grass
                1,   //Dead Bush
                304,     //Piston
                1,   //Piston Head
                24,  //Wool
                1,   //Moving Block
                16,  //Dandelion
                16,  //Rose
                192,     //Brown Mushroom
                192,     //Red Mushroom
                13671,   //Gold Block
                1422,    //Iron Block
                2,   //Double Slab
                1,   //Stone Slab
                279,     //Brick Block
                680,     //TNT
                336,     //Bookshelf
                13207,   //Moss Stone
                80,  //Obsidian
                12,  //Torch
                4,   //Fire
                330175,  //Monster Spawner
                12,  //Wooden Stairs
                64,  //Chest
                146,     //Redstone
                3566,    //Diamond Ore
                32094,   //Diamond Block
                32,  //Crafting Table
                24,  //Wheat
                1,   //Soil
                8,   //Furnace
                8,   //Burning Furnace
                52,  //Sign
                48,  //Wooden Door
                28,  //Ladder
                59,  //Rails
                1,   //Cobblestone Stairs
                52,  //Sign
                5,   //Lever
                2,   //Stone Pressure Plate
                948,     //Iron Door
                16,  //Wooden Pressure Plate
                655,     //Redstone Ore
                655,     //Glowing Redstone Ore
                150,     //Redstone Torch (off)
                150,     //Redstone Torch
                2,   //Stone Button
                1,   //Snow
                1,   //Ice
                1,   //Snow Block
                96,  //Cactus
                237,     //Clay Block
                32,  //Sugar Cane
                3630,    //Jukebox
                12,  //Fence
                192,     //Pumpkin
                1,   //Netherrack
                760,     //Soul Sand
                3038,    //Glowstone
                0,   //Portal
                192,     //JackOLantern
                173,     //Cake
                437,     //Redstone Repeater (off)
                437,     //Redstone Repeater
                0,   //Locked Chest
                0,   //Trapdoor
                0,   //Stone (Silverfish)
                12,  //Stone Brick
                0,   //Red Mushroom Cap
                0,   //Brown Mushroom Cap
                59,  //Iron Bars
                0,   //Glass Pane
                0,   //Melon Block
                0,   //Pumpkin Stem
                0,   //Melon Stem
                0,   //Vines
                0,   //Fence Gate
                0,   //Brick Stairs
                0,   //Stone Brick Stairs
                1,   //Mycelium
                3566,    //Lily Pad
                46,  //Nether Brick
                0,   //Nether Brick Fence
                0,   //Nether Brick Stairs
                0,   //Nether Wart
                7452,    //Enchantment Table
                360,     //Brewing Stand
                1106,    //Cauldron
                0,   //End Portal
                0,   //End Portal Frame
                1,   //End Stone
                320940,  //Dragon Egg
                3620,    //Redstone Lamp
                3620,    //Redstone Lamp (on)
                0,   //Double Wooden Slab
                0,   //Wooden Slab
                0,   //Cocoa Plant
                0,   //Sandstone Stairs
                3038,    //Emerald Ore
                2584,    //Ender Chest
                158,     //Tripwire Hook
                69,  //Tripwire
                27342,   //Emerald Block
                0,   //Stairs
                0,   //Stairs
                0,   //Stairs
                0,   //Command Block
                320940,  //Beacon
                1,   //Cobblestone Wall
                79,  //Flower Pot
                0,   //Carrots
                0,   //Potatoes
                2,   //Wooden Button
                23,  //Head
                4898, //Anvil
                222 , //Trapped Chest
                3038, //Weighted Pressure Plate (light)
                316 , //Weighted Pressure Plate (heavy)
                607 , //Redstone Comparator (inactive)
                607 , //Redstone Comparator (active)
                503 , //Daylight Sensor
                1310, //Redstone Block
                158 , //Nether Quartz Ore
                854 , //Hopper
                632 , //Quartz Block
                948 , //Quartz Stairs
                84  , //Activator Rail
                153 , //Dropper
                248 , //Stained Clay
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                216 , //Hay Bale
                16  , //Carpet
                248 , //Hardened Clay
                756 , //Block of Coal
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0   , //
                0    //
        };
    }
    
    /**
     * The idea behind this method is to take a list of Blocks and pull all the ids.
     * It really only exists to cut down on the number of ".blockID" that is in this file given
     * how long it will be.  
     * @param blockList
     */
    public static ArrayList<String> loadBlockIds(Block[] blockList) {
        ArrayList<String> IDs = new ArrayList<String>();
        for(Block block : blockList)
            IDs.add(block.getBlockById());
        return IDs;
    }
    
    public static int getEnergy(int blockID){
        if(blockID > 255)
            return 1;
        return blockEnergy[blockID];
    }

    public static int getTier(int blockID){
        int energy = getEnergy(blockID);
        energy = energy < 1 ? 1 : energy; // log(0) = crash bad
        return (int) Math.round(Math.log(energy) / Math.log(2));
    }

    public static int energyToRadiusConversion(int energy) {
        int diameter = 1;
        while( diameter * diameter * diameter * blockMoveCost < energy) //this is over generous intentionally
            diameter += 2; // +2 so that we always have an odd number and have block centered shapes
        return diameter/2; //integer math will round down the .5
    }
    
    
    /**
     * naturalBlocks is an important list because it lists all blocks that will not conduct runic energy
     */
    public static boolean isNatural(Block blockID){
        return naturalBlocks.contains(new Block(blockID));
    }
    
    /**
     * This is a list of all the blocks that need special treatment when moving groups of blocks
     * like FTP or Runecraft.  All independent blocks need to be placed first because all of these
     * blocks attach to other blocks or (in the case of liquids) need to be held in by solid blocks.
     * @param blockID
     */
    public static boolean isMoveSensitive(Block blockID){
        return moveSensitiveBlocks.contains(new Block(blockID));
    }

    public static boolean isCrushable(Block blockID) {
        return crushableBlocks.contains(new Blocks(blockID));
    }
}


