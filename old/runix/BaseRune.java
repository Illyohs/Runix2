package com.newlinegaming.runix;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.newlinegaming.runix.block.FuelBlock;
import com.newlinegaming.runix.block.NoneBlock;
import com.newlinegaming.runix.block.SignatureBlock;
import com.newlinegaming.runix.block.TierBlock;
import com.newlinegaming.runix.handlers.RuneHandler;
import com.newlinegaming.runix.rune.WaypointRune;
import com.newlinegaming.runix.utils.Util_Movement;

/**
 * This class contains the basic functions that runes will use to execute their functions.  Any reusable code or concepts should go in
 * BaseRune and not in the individual runes.  This will make it easy to create new and custom runes as well as making the child classes
 * as thin as possible.
 */


//TODO: Rename to BaseRune as this is the base of all runes
public abstract class BaseRune
{
    //TODO: Get rid of  unnecessary comments all comments that interrupt the code style

    public static final Block TIER = new TierBlock(); //Tier
    public static final Block SIGR = new SignatureBlock(); //Signature block
    public static final Block NONE = new NoneBlock(); //Non-Tier, Tier 0
    //Please note: putting 0 in a blockPattern() requires AIR, not simply Tier 0
    public static final Block FUEL = new FuelBlock(); //required to be in the middle of the rune
    public int energy = 0;
    public String runeName          = null;
    public String runeLocalizedName = null;

    public boolean usesConductance = false;

    public BaseRune()
    {
    }

    /**
     * This method takes the player and the rune, and verifies that a rune can be used. to go with perms/disabled runes.txt or whatever
     *
     * @param player - the caster
     * @param rune   - the rune being cast
     * @return
     */
    protected static boolean runeAllowed(EntityPlayer player, BaseRune rune)
    {
        // arbi
//		player.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN+rune.getRuneName()+ " accepted"));
//        player.addChatComponentMessage(IChatComponent);
        return true;
    }

    public static void aetherSay(EntityPlayer player, String message)
    {

        if (!player.worldObj.isRemote && player != null)
        {
            player.addChatMessage(new TextComponentString(message));
        } else
        {
            System.out.println(message);
        }
    }

    /**
     * Required implementation to determine what arrangement of blocks maps to your rune.  Once this is
     * defined in your class, never use it.  Use runicFormulae() instead.
     */
    protected abstract Block[][][] runicTemplateOriginal();

    public abstract boolean isFlatRuneOnly();

    /**
     * Use this method to check Rune template compliance, not runicTemplateOriginal().
     * This method will take the facing of coords and use it to match orientation for vertical runes.
     *
     * @param coords world coordinates and facing to check against the rune
     * @return WorldPos is the coordinates being checked.  Use WorldPos.getBlockID().  SigBlock is
     * the runeTemplate for that block, which can be special values like TIER or KEY.
     */
    protected HashMap<WorldPos, IBlockState> runicFormulae(WorldPos coords)
    {
        if (isFlatRuneOnly())
            coords = coords.copyWithNewFacing(1); //we need a new object so we don't side-effect other runes
        return patternToShape(runicTemplateOriginal(), coords);
    }

    /**
     * Executes the main function of a given Rune.  If the Rune is persistent, it will store XYZ and other salient
     * information for future use.  Each Rune class is responsible for keeping track of the information it needs in
     * a static class variable.
     *
     * @param coords  World and xyz that Rune was activated in.
     * @param player  We pass the player instead of World so that runes can later affect the Player
     * @param forward
     */
    public void execute(WorldPos coords, EntityPlayer player, Vector3 forward)
    {
        execute(coords, player); //Instant runes drop the forward parameter by default
    }

    public abstract void execute(WorldPos coords, EntityPlayer player);

    /**
     * This method takes a 3D block Pattern and simply stamps it on the world with coordinates centered on WorldPos.
     * It should only be used on shapes with odd numbered dimensions.  This will also delete blocks if the template
     * calls for 0 (AIR).
     *
     * @param pattern The blockPattern to be stamped.
     * @param player  used to check for build permissions.  Player also provides worldObj.
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return Returns false if the operation was blocked by build protection.  Currently always true.
     */
    protected boolean stampBlockPattern(HashMap<WorldPos, IBlockState> stamp, EntityPlayer player)
    {
        for (WorldPos target : stamp.keySet())
            target.setBlock(stamp.get(target.getBlock().));
        return true;
        //TODO: build permission checking
    }

    /**
     * This will safely teleport the player by scanning in the coords.face direction for 2 AIR blocks that drop the player
     * less than 20 meters onto something that's not fire or lava.
     * This method should be used for any teleport or similar move that may land the player in some blocks.
     *
     * @param player
     * @param coords    Target destination
     * @throws NotEnoughRunicEnergyException
     */
    protected void teleportPlayer(EntityPlayer player, WorldPos coords) throws NotEnoughRunicEnergyException
    {

        Vector3 direction = Vector3.facing[coords.face];
        for (int tries = 0; tries < 100; ++tries)
        {
            if ((coords.getY() < 255 && coords.getY() > 0) // coords are in bounds
                    && coords.getWorld().getBlockState(coords).getBlock() == Blocks.AIR
                    && coords.getWorld().getBlockState(coords).getBlock() == Blocks.AIR)//two AIR blocks
            {
                for (int drop = 1; drop < 20 && coords.getY() - drop > 0; ++drop)//less than a 20 meter drop
                {//begin scanning downward
                    Block block = coords.getWorld().getBlockState(new BlockPos(coords.getX(), coords.getY(), coords.getZ())).getBlock()
                    if (block != Blocks.AIR)
                    { //We found something not AIR
                        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA || block == Blocks.FIRE)
                        {//if we teleport now, the player will land on an unsafe block
                            break; //break out of the drop loop and proceed on scanning a new location
                        } else if (coords.offset(0, -drop, 0).isSolid())
                        { //we're going to land on something solid, without dying
                            //distance should be calculated uses the Nether -> Overworld transform
                            WorldPos dCalc = new WorldPos(player);
                            if (player.worldObj.provider.isHellWorld && !coords.getWorld().provider.isHellWorld)
                            { //leaving the Nether
                                dCalc.getX() *= 8;
                                dCalc.posZ *= 8;
                            } else if (!player.worldObj.provider.isHellWorld && coords.getWorld().provider.isHellWorld)
                            {// going to the Nether
                                dCalc.posX /= 8;
                                dCalc.posZ /= 8;
                            }
                            spendEnergy((int) (coords.getDistance(dCalc) * Tiers.movementPerMeterCost));

                            if (!coords.getWorld().equals(player.worldObj))// && !subject.worldObj.isRemote)
                                player.travelToDimension(coords.getWorld().provider.dimensionId);
                            player.setPositionAndUpdate(coords.posX + 0.5, coords.posY, coords.posZ + 0.5);
                            return;
                        }//we've found something that's not AIR, but it's not dangerous so just pass through it and keep going
                    }
                }
            }
            coords = coords.offset(direction);
        }
        aetherSay(player, "There was no safe place to put your character.");
    }

    /**
     * returns the unique name of the rune
     */
    public String getRuneName()
    {

        if (!runeName.isEmpty())
        {
            return runeName;
        } else
        {
            return shortClassName();
        }
    }

    /**
     * Used to get rune names from the .lang file
     */
    public String getLocalizedRuneName()
    {
        return runeLocalizedName;
    }

    public void aetherSay(World worldObj, String message)
    {

        if (!worldObj.isRemote)
        { //[6915f56] Fixed player messages by just sending them from the server side instead of the ignorant client.
            Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
        } else
        {
            System.out.println(message);
        }
    }

    /**
     * Checks to see if there is a block match for the Rune blockPattern center at
     * WorldPos coords.
     * Legacy Note: RunixMain changed rune pattern recognition to accept T0 ink blocks
     * and T1+ None Corners.  So if there is a recognizable shape, it will be accepted.
     *
     * @return true if there is a valid match
     */
    public WorldPos checkRunePattern(WorldPos coords)
    {
        HashMap<WorldPos, SigBlock> shape = runicFormulae(coords);
        if (!isAssymetrical())
        {
            if (runeOrientationMatches(coords, shape))
                return coords;
            else
                return null;
        } else
        {
            for (int nTurns = 0; nTurns < 4; ++nTurns)
            {//90 degree turns
                HashMap<WorldPos, IBlockState> newShape = Util_Movement.rotateStructureInMemory(shape, coords, nTurns);
                if (runeOrientationMatches(coords, newShape))
                {
                    //change coords to be pointing in the detected direction, [array lookup]
                    switch (coords.face)
                    {
                        case 0:
                        case 1:
                            coords.face = (new ArrayList<Vector3>(Arrays.asList(Vector3.facing))).indexOf(Vector3.xzRotationOrder[nTurns]);
                            break;
                        case 2:
                        case 3:
                            coords.face = (new ArrayList<Vector3>(Arrays.asList(Vector3.facing))).indexOf(Vector3.xyRotationOrder[nTurns]);
                            break;
                        case 4:
                        case 5:
                            coords.face = (new ArrayList<Vector3>(Arrays.asList(Vector3.facing))).indexOf(Vector3.yzRotationOrder[nTurns]);
                            break;
                    }
                    return coords;
                }
            }
        }
        return null;
    }

    public boolean runeOrientationMatches(WorldPos coords, HashMap<WorldPos, IBlockState> shape)
    {
        Block ink = getTierInkBlock(coords);
        if (ink == Blocks.air)
            return false; //Tier blocks cannot be AIR
//        printPattern(shape, coords);
        for (WorldPos target : shape.keySet())
        {
            Block       blockID   = target.getBlock();
            IBlockState patternID = shape.get(target);
//            System.out.println(patternID.blockID + " should be " + blockID);
            switch (patternID.getBlock().getUnlocalizedName())
            { // Handle special Template Values
                case "tile.NONE":
                    if (blockID == ink)
                        return false;
                    break;
                case "tile.TIER":
                    if (blockID != ink)
                    {
                        return false; //inconsistent Tier block
                    }
                    break;
                case "tile.SIGR":
                    if (blockID == ink)
                        return false; //you can't use your ink as part of your signature, it ruins the shape
                    break;
                case "tile.FUEL":
                    if (!target.equals(coords) || blockID == Blocks.air)//key block must be center block and not AIR
                        return false;
                    break;
                default:
                    if (!patternID.equals(blockID))
                    {//normal block
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    private void printPattern(HashMap<WorldPos, IBlockState> shape, WorldPos coords)
    {
        Vector3[] neighbors = {
                new Vector3(-1, 0, -1),
                new Vector3(0, 0, -1),
                new Vector3(1, 0, -1),
                new Vector3(-1, 0, 0),
                new Vector3(0, 0, 0),
                new Vector3(1, 0, 0),
                new Vector3(-1, 0, 1),
                new Vector3(0, 0, 1),
                new Vector3(1, 0, 1)};

        try
        {
            for (int z = -1; z <= 1; ++z)
            {
                for (int x = -1; x <= 1; ++x)
                {
                    char ch = 'O';
                    if (shape.get(coords.offset(x, 0, z)).equals(Blocks.iron_block))
                        ch = '#';
                    System.out.print(ch);
                }
                System.out.println();//newline
            }
        } catch (Exception e)
        {
        }
    }

    protected int getTier(WorldPos coords)
    {
        Block blockID = getTierInkBlock(coords);
        return blockID != null ? Tiers.getTier(blockID) : 1;
    }

    protected Block getTierInkBlock(WorldPos coords)
    {
        HashMap<WorldPos, IBlockState> shape = runicFormulae(coords);
        for (WorldPos target : shape.keySet())
        {
            if (shape.get(target).equals(TIER))
            {
                return target.getBlock();
            }
        }
        return null; // There was no TIER mentioned in the pattern
    }

    /**
     * Call accept() once you are sure the rune will be executed to tell the player it was successful.
     */
    protected void accept(EntityPlayer player)
    {
        aetherSay(player, TextFormatting.GREEN + getRuneName() + " Accepted.");
    }

    /**
     * This will return an empty list if the activation would tear a structure in two.
     */
    public HashSet<WorldPos> conductanceStep(WorldPos startPoint, int maxDistance)
    {
        HashSet<WorldPos> workingSet = new HashSet<WorldPos>();
        HashSet<WorldPos> activeEdge;
        HashSet<WorldPos> nextEdge   = new HashSet<WorldPos>();
        workingSet.add(startPoint);
        nextEdge.add(startPoint);

        for (int iterationStep = maxDistance + 1; iterationStep > 0; iterationStep--)
        {
            activeEdge = nextEdge;
            nextEdge = new HashSet<WorldPos>();
            //tear detection: this should be empty by the last step
            if (iterationStep == 1 && activeEdge.size() != 0)
                return new HashSet<WorldPos>();

            for (WorldPos block : activeEdge)
            {
                ArrayList<WorldPos> neighbors = block.getNeighbors();
                for (WorldPos n : neighbors)
                {
                    Block blockID = n.getBlock();
                    // && blockID != 0 && blockID != 1){  // this is the Fun version!
                    if (!workingSet.contains(n) && !Tiers.isNatural(blockID))
                    {
                        workingSet.add(n);
                        nextEdge.add(n);
                    }
                }
            }
        }
        return workingSet;
    }

    public void moveMagic(HashMap<WorldPos, WorldPos> positionsMoved)
    {
        // Default behavior is nothing. Override this for persistent runes
    }

    /**
     * This is essentially a way to make iterating over blockPatterns much
     * easier by enabling a single for loop: for(WorldPos target : shape.keySet)
     * blockPattern() "iterator" actually a HashMap<WorldPos, SigBlock>. The
     * WorldPos serves as a comparison for world coordinates and the world block
     * can be had through WorldPos.getBlockID(). The HashMap SigBlock is
     * actually the runic formulae including special values like TIER.
     *
     * @param pattern
     * @param centerPoint
     * @return
     */
    protected HashMap<WorldPos, IBlockState> patternToShape(Block[][][] pattern, WorldPos centerPoint)
    {
        // World coordinates + relative offset + half the size of the rune (for middle)
        // "-y" the activation and "center" block for 3D runes is the top layer, at the moment
        HashMap<WorldPos, IBlockState> shape = new HashMap<WorldPos, IBlockState>();
        for (int y = 0; y < pattern.length; y++)
        {
            for (int z = 0; z < pattern[y].length; z++)
            {
                for (int x = 0; x < pattern[y][z].length; x++)
                {
                    WorldPos target;
                    //switch on different orientations
                    switch (centerPoint.face)
                    {
                        case 1: //laying flat activated from top or bottom
                        case 0:
                            target = centerPoint.offset(-pattern[y][z].length / 2 + x, -y, -pattern[y].length / 2 + z);//TODO: clockwise vs CCW?
                            break;
                        case 2://NORTH or SOUTH which points along the z axis
                        case 3://this means that flat runes (XZ runes) will extend along XY
                            target = centerPoint.offset(-pattern[y][z].length / 2 + x, pattern[y].length / 2 - z, -y);//TODO: +y for SOUTH
                            break;
                        case 4://WEST or EAST facing
                        case 5://flat runes extend along the ZY plane
                            target = centerPoint.offset(-y, pattern[y][z].length / 2 - x, -pattern[y].length / 2 + z);
                            break;
                        default:
                            System.err.println("Block facing not recognized: " + centerPoint.face + " should be 0-5.");
                            target = centerPoint;
                    }
                    if (pattern[y][z][x] != NONE)
                    { //do not include NONE blocks in the runic template at all.
                        shape.put(target, new SigBlock(pattern[y][z][x], 0));
                    }
                }
            }
        }
        return shape;
    }

    /**
     * Removes the shape and adds its block energy to the rune
     */
    protected void consumeRune(WorldPos coords)
    {
        if (isFlatRuneOnly())
            coords = coords.copyWithNewFacing(1);
        HashMap<WorldPos, IBlockState> shape = runicFormulae(coords);
        for (WorldPos target : shape.keySet())
        {
            //for each block, get blockID
            Block blockID = target.getBlock();
            energy += Tiers.getEnergy(blockID);//convert ID into energy
            target.setBlock(Blocks.air);// delete the block
        }
        System.out.println(getRuneName() + " energy: " + energy);
    }

    /**
     * Removes the shape and adds its block energy to the rune
     */
    protected void consumeRune(Collection<WorldPos> shape)
    {
        for (WorldPos target : shape)
        {
            Block blockID = target.getBlock();
            energy += Tiers.getEnergy(blockID);//convert ID into energy
            target.setBlock(Blocks.air);// delete the block
        }
        System.out.println(getRuneName() + " energy: " + energy);
    }

    public void setBlockIdAndUpdate(WorldPos coords, Block blockID) throws NotEnoughRunicEnergyException
    {
        if (blockID == Blocks.air)//this is actually breaking, not paying for air
            spendEnergy(Tiers.blockBreakCost);
        else
            spendEnergy(Tiers.getEnergy(blockID));
        coords.setBlock(blockID);
    }

    public void setBlockIdAndUpdate(WorldPos destination, IBlockState sourceBlock) throws NotEnoughRunicEnergyException
    {
        if (sourceBlock.getBlock() == Blocks.air)//this is actually breaking, not paying for air
            spendEnergy(Tiers.blockBreakCost);
        else
            spendEnergy(Tiers.getEnergy(sourceBlock.getBlock()));
        destination.setBlock(sourceBlock.getBlock());
    }

    /**
     * @param energyCost
     * @throws NotEnoughRunicEnergyException
     */
    protected void spendEnergy(int energyCost) throws NotEnoughRunicEnergyException
    {
        if (energy < energyCost)
        {
            throw new NotEnoughRunicEnergyException();
        }
        energy -= energyCost;
    }

    /**
     * This is a minature convenience version of moveShape(moveMapping) for single blocks
     *
     * @throws NotEnoughRunicEnergyException
     */
    public void moveBlock(WorldPos coords, WorldPos newPos) throws NotEnoughRunicEnergyException
    {
        newPos.setBlockId(coords.getSigBlock());
        coords.setBlock(Blocks.air);
        spendEnergy((int) Tiers.blockMoveCost);

        HashMap<WorldPos, WorldPos> moveMapping = new HashMap<WorldPos, WorldPos>(1, 1.0f);//tiny HashMap!
        moveMapping.put(coords, newPos);
        RuneHandler.getInstance().moveMagic(moveMapping);
    }

    protected boolean consumeFuelBlock(WorldPos coords)
    {
        if (Tiers.getTier(coords.getBlock()) > 1)
        {
            energy += Tiers.getEnergy(coords.getBlock());
            coords.setBlock(Blocks.cobblestone);//we don't want air sitting here
            return true;
        }
        return false;
    }

    protected String shortClassName()
    {
        return this.getClass().toString().replace("class com.newlinegaming.Runix.rune.", "");
    }

    public WorldPos findWaypointBySignature(EntityPlayer poker, Signature signature)
    {
        //new WaypointRune() is necessary because getActiveMagic() CANNOT be static, so it returns a pointer to a static field...
        ArrayList<PersistentRune> waypointList = (new WaypointRune().getActiveMagic());
        PersistentRune            wp           = null;
        for (PersistentRune candidate : waypointList)
        {
            if (new Signature(candidate, candidate.location).equals(signature)
                    && candidate.runeIsIntact())
            {
                wp = candidate;
                break;
            }
        }
        if (wp == null)
        {
            aetherSay(poker, "A waypoint with that signature cannot be found.");
            return null;
        }
        WorldPos destination = new WorldPos(wp.location);
        return destination;
    }

    /*
     * Placeholder which returns an empty signature.  Ovverride this to add signatures to your rune.
     */
    public Signature getSignature()
    {
        return new Signature();
    }

    public int authority()
    {
        return 0;
    }

    public boolean isAssymetrical()
    {
        return false;
    }

    public HashSet<WorldPos> runeBlocks(WorldPos coords)
    {
        return new HashSet<WorldPos>(runicFormulae(coords).keySet());
    }
}
