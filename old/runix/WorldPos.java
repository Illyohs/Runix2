package com.newlinegaming.runix;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.server.FMLServerHandler;

;


/**
 * This class was created for Runix to ensure that when transferring between sets of coordinates,
 * the World is always known.  It extends ChunkCoordinates used by the rest of minecraft, but tracks
 * World and contains helper methods useful to Runix.
 */
public class WorldPos extends BlockPos
{

    public            int   face        = 1;
    private transient World world       = null;
    private           int   dimensionID = -500000;

    public WorldPos(int x, int y, int z)
    {
        super(x, y, z);
        this.setWorld(defaultWorld());
    }

    public WorldPos(World world, int x, int y, int z)
    { //this constructor was made to be fast
        super(x, y, z);
        dimensionID = world.provider.getDimension();
    }


    public WorldPos(World world, int x, int y, int z, int face)
    {
        super(x, y, z);
        this.setWorld(world);
        this.face = face;
    }

    public WorldPos(EntityPlayer player)
    {
        super((int) (player.posX + .5), (int) (player.posY - 1), (int) (player.posZ + .5));
        setWorld(player.worldObj);
    }

    public WorldPos(WorldPos otherGuy)
    {
        super(otherGuy);
        if (otherGuy instanceof WorldPos)
        {
            this.setWorld((otherGuy).getWorld());
            face = (otherGuy).face;
        } else
            this.setWorld(defaultWorld());
    }

    private static World defaultWorld()
    {
        return FMLServerHandler.instance().getServer().worldServerForDimension(0);
    }

    public World getWorld()
    {
        if (world == null && dimensionID != -500000)
        {
            setWorld(dimensionID);
        }
        return world;
    }

    /**
     * This is for loadRunes() from JSON.  We need to set the WorldObj off
     * of the dimension number.
     *
     * @param dimension
     */
    public void setWorld(int dimension)
    {
        world = FMLServerHandler.instance().getServer().worldServerForDimension(dimension);
        dimensionID = getDimensionNumber();
    }

    public void setWorld(World world)
    {
        this.world = world;
        dimensionID = getDimensionNumber();
    }

    /**
     * Creates a new WorldPos based off of a previous one and a relative vector
     */
    public WorldPos offset(int dX, int dY, int dZ)
    {
        return new WorldPos(this.getWorld(), this.getX() + dX, this.getY() + dY, this.getZ() + dZ, face);
    }

    public WorldPos offset(int dX, int dY, int dZ, int facing)
    {
        return new WorldPos(this.getWorld(), this.getX() + dX, this.getY() + dY, this.getZ() + dZ, facing);
    }

    public WorldPos offset(Vector3 delta)
    {
        return new WorldPos(this.getWorld(), getX() + delta.x, getY() + delta.y, getZ() + delta.z, face);
    }

    public WorldPos offsetWorld(Vector3 delta, World dem)
    {
        return new WorldPos(dem, getX() + delta.x, getY() + delta.y, getZ() + delta.z, face);
    }

    /**
     * Like offset() but for facing instead.  Returning a new instance avoids side-effecting
     */
    public WorldPos copyWithNewFacing(int face2)
    {
        WorldPos n = new WorldPos(this);
        n.face = face2;
        return n;
    }

    /**
     * Similar to offset(), but updates the current instance instead of a new one.
     */
    public WorldPos bump(int dX, int dY, int dZ)
    {
        int x = getX();
        int y = getY();
        int z = getZ();
        x += dX;
        y += dY;
        z += dZ;
        return this;
    }

    public WorldPos rotate(WorldPos referencePoint, boolean counterClockwise)
    {
        Vector3 d         = new Vector3(referencePoint, this);// determine quadrant relative to reference
        int     direction = counterClockwise ? -1 : 1;
        //handle facing rotation:
//        int index = Vector3.xzRotationOrder.indexOf(new Integer(referencePoint.face));
//        if(index > -1) //not up or down
//            face = Vector3.xzRotationOrder.get( (index+direction ) % 4 );
        //Josiah: you have no idea how hard it was to get this one line of code
        if (referencePoint.face == 1 || referencePoint.face == 0)//UP or DOWN, xz rotation
            return referencePoint.offset(direction * -d.z, d.y, direction * d.x, face);
        if (referencePoint.face == 2 || referencePoint.face == 3)//North South,  XY rotation
            return referencePoint.offset(direction * d.y, direction * -d.x, d.z, face);
        //East or West  YZ rotation
        return referencePoint.offset(d.x, direction * -d.z, direction * d.y, face);
    }

    private int getDimensionNumber()
    {
        if (getWorld() == null)
            setWorld(defaultWorld());
        return getWorld().provider.getDimension();
    }

//    @Override
//    public boolean equals(Object otherObj)
//    {
//        if (!(otherObj instanceof ChunkCoordinates)){
//            return false;
//        }
//        else{
//            ChunkCoordinates other = (ChunkCoordinates)otherObj;
//            if( this.posX == other.posX && this.posY == other.posY && this.posZ == other.posZ){
//                if(other instanceof WorldPos)
//                    return ((WorldPos) other).getWorld() == this.getWorld();
//                else //NOTE: This does not compare the face of each coordinate
//                    return true;
//            }
//            return false;
//        }
//    }

//    public SigBlock getSigBlock() {
//        return new SigBlock(getBlock(), getMetaId());
//    }

    //Simple wrapper method for getBlockID()
    public Block getBlock()
    {
        return this.getWorld().getBlockState(new WorldPos(this.getX(), this.getY(), this.getZ())).getBlock();
    }

    public Block getBlock(WorldPos pos)
    {
        return this.getWorld().getBlockState(pos).getBlock();
    }

    /**
     * Simple wrapper method for setBlockID()
     *
     * @param block
     * @return true if successful
     */
    public boolean setBlock(Block block)
    {
        if (block != Blocks.BEDROCK || getBlock() != Blocks.BEDROCK)
        {
            this.getWorld().setBlockState(new WorldPos(getX(), getY(), getZ()), block.getDefaultState());
        }
        return false;
    }

//    public boolean setBlockId(SigBlock sig){
//        if(sig.equals(Blocks.bedrock) || getBlock() == Blocks.bedrock)
//            return false; //You cannot delete or place bedrock
//        return this.getWorld().setBlock(posX, posY, posZ, sig.blockID, sig.meta, 2);
//        //NOTE: Use last arg 3 if you want a block update.
//    }


    public String toString()
    {
        return "{\"dimensionID\":" + dimensionID + ",\"face\":" + face + ",\"posX\":" + getX() + ",\"posY\":" + getY() + ",\"posZ\":" + getZ() + "}";
    }

    public ArrayList<WorldPos> getDirectNeighbors()
    {
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        //6 cardinal sides
        neighbors.add(offset(0, 1, 0));
        neighbors.add(offset(0, -1, 0));
        neighbors.add(offset(0, 0, -1));
        neighbors.add(offset(1, 0, 0));
        neighbors.add(offset(0, 0, 1));
        neighbors.add(offset(-1, 0, 0));
        return neighbors;
    }

    public ArrayList<WorldPos> getNeighbors()
    {
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        //6 cardinal sides
        neighbors.add(offset(0, 1, 0));
        neighbors.add(offset(0, -1, 0));
        neighbors.add(offset(0, 0, -1));
        neighbors.add(offset(1, 0, 0));
        neighbors.add(offset(0, 0, 1));
        neighbors.add(offset(-1, 0, 0));

        //12 edge diagonals
        //Josiah: If there was a way to get Build Master and Runecraft to cooperate without these
        //extra 12 checks I would really rather only do 1/3 the workload when loading large Runecraft
        //structures
        neighbors.add(offset(1, 1, 0));
        neighbors.add(offset(-1, 1, 0));
        neighbors.add(offset(0, 1, 1));
        neighbors.add(offset(0, 1, -1));

        neighbors.add(offset(1, 0, 1));
        neighbors.add(offset(-1, 0, 1));
        neighbors.add(offset(1, 0, -1));
        neighbors.add(offset(-1, 0, -1));

        neighbors.add(offset(1, -1, 0));
        neighbors.add(offset(-1, -1, 0));
        neighbors.add(offset(0, -1, 1));
        neighbors.add(offset(0, -1, -1));
        //the 8 corner diagonals are not included
        return neighbors;
    }

    public ArrayList<WorldPos> getNeighbors(Vector3 orientation)
    {
        int                 x         = 0;
        int                 y         = 0;
        int                 z         = 0;
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        if (Math.abs(orientation.x) == 1)
        {
            for (z = -1; z <= 1; ++z)
            {
                for (y = -1; y <= 1; ++y)
                {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        if (Math.abs(orientation.y) == 1)
        {
            for (z = -1; z <= 1; ++z)
            {
                for (x = -1; x <= 1; ++x)
                {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        if (Math.abs(orientation.z) == 1)
        {
            for (y = -1; y <= 1; ++y)
            {
                for (x = -1; x <= 1; ++x)
                {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        return getNeighbors();
    }

    public double getDistance(WorldPos other)
    {
        double xzDist_2 = (getX() - other.getX()) * (getX() - other.getX()) + (getZ() - other.getZ()) * (getZ() - other.getZ());
        return Math.sqrt(xzDist_2 + (getY() - other.getY()) * (getY() - other.getY()));
    }

    public boolean isSolid(IBlockState state)
    {
        Material base = getBlock().getMaterial(state);
        return base.isSolid();
    }

    public void bump(Vector3 vec)
    {
        bump(vec.x, vec.y, vec.z);
    }


//    @Override
//    public int compareTo(Object o) {
//        return 0;
//    }

    public boolean isCrushable()
    {
        return Tiers.isCrushable(getBlock());
    }

}
