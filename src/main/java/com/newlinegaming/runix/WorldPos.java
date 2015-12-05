package com.newlinegaming.runix;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * This class was created for Runix to ensure that when transferring between
 * sets of coordinates, the World is always known. It extends ChunkCoordinates
 * used by the rest of minecraft, but tracks World and contains helper methods
 * useful to Runix.
 */

// Note might want to move to a library mod

public class WorldPos extends BlockPos {

    private transient World worldObj    = null;
    private int             dimensionID = -500000;
    public int              face        = 1;

    // public WorldXYZ() { //default world is causing an issue with servers
    // because defaultWorld() doesn't work correctly
    // this.posX = 0;
    // this.posY = 64;
    // this.posZ = 0;
    // this.setWorld(defaultWorld());
    // }
    //
    public WorldPos(int x, int y, int z) {
        super(x, y, z);
        this.setWorld(defaultWorld());
    }

    public WorldPos(World world, int x, int y, int z) { //this constructor was made to be fast
        super(x, y, z);
        worldObj = world;
        dimensionID = world.provider.getDimensionId();
    }

    public WorldPos(World world, int x, int y, int z, int face) {
        super(x, y, z);
        this.setWorld(world);
        this.face = face;
    }

    public WorldPos(EntityPlayer player) {
        super((int) (player.posX + .5), (int) (player.posY - 1), (int) (player.posZ + .5));
        setWorld(player.worldObj);
    }

    public WorldPos(BlockPos otherGuy) {
        super(otherGuy);
        if (otherGuy instanceof WorldPos) {
            this.setWorld(((WorldPos) otherGuy).getWorld());
            face = ((WorldPos) otherGuy).face;
        } else {
            this.setWorld(defaultWorld());
        }
    }

    public World getWorld() {
        if (worldObj == null && dimensionID != -500000) {
            setWorld(dimensionID);
        }
        return worldObj;
    }


    public void setWorld(World worldObj) {
        this.worldObj = worldObj;
        dimensionID = getDimensionNumber();
    }

    /**
     * This is for loadRunes() from JSON. We need to set the WorldObj off of the
     * dimension number.
     * 
     * @param dimension
     */
    public void setWorld(int dimension) {
        worldObj = MinecraftServer.getServer().worldServerForDimension(dimension);
        // worldObj =
        // FMLServerHandler.instance().getServer().worldServerForDimension(dimension);
        
        dimensionID = getDimensionNumber();
    }

    /**
     * Creates a new WorldXYZ based off of a previous one and a relative vector
     */
    public WorldPos offset(int dX, int dY, int dZ) {
        return new WorldPos(this.getWorld(), this.getX() + dX, this.getY() + dY, this.getZ() + dZ, face);
    }

    public WorldPos offset(int dX, int dY, int dZ, int facing) {
        return new WorldPos(this.getWorld(), this.getX() + dX, this.getY() + dY, this.getZ() + dZ, facing);
    }

    public WorldPos offset(Vector3 delta) {
        return new WorldPos(this.getWorld(), this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z, face);
    }

    public WorldPos offsetWorld(Vector3 delta, World dem) {
        return new WorldPos(dem, this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z, face);
    }

    /**
     * Like offset() but for facing instead. Returning a new instance avoids
     * side-effecting
     */
    public WorldPos copyWithNewFacing(int face2) {
        WorldPos n = new WorldPos(this);
        n.face = face2;
        return n;
    }

    /**
     * Similar to offset(), but updates the current instance instead of a new
     * one.
     */
    public WorldPos bump(int dX, int dY, int dZ) {
        x += dX;
        y += dY;
        z += dZ;
        
        return this;
    }

    public WorldPos rotate(WorldPos referencePoint, boolean counterClockwise) {
        Vector3 d = new Vector3(referencePoint, this);// determine quadrant
                                                      // relative to reference
        int direction = counterClockwise ? -1 : 1;
        // handle facing rotation:
        // int index = Vector3.xzRotationOrder.indexOf(new
        // Integer(referencePoint.face));
        // if(index > -1) //not up or down
        // face = Vector3.xzRotationOrder.get( (index+direction ) % 4 );
        // Josiah: you have no idea how hard it was to get this one line of code
        if (referencePoint.face == 1 || referencePoint.face == 0)// UP or DOWN,
                                                                 // xz rotation
            return referencePoint.offset(direction * -d.z, d.y, direction * d.x, face);
        if (referencePoint.face == 2 || referencePoint.face == 3)// North South,
                                                                 // XY rotation
            return referencePoint.offset(direction * d.y, direction * -d.x, d.z, face);
        // East or West YZ rotation
        return referencePoint.offset(d.x, direction * -d.z, direction * d.y, face);
    }

    public int getDimensionNumber() {
        if (getWorld() == null)
            setWorld(defaultWorld());
        return getWorld().provider.getDimensionId();
    }

    public static World defaultWorld() {
        return MinecraftServer.getServer().worldServerForDimension(0);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof BlockPos)) {
            return false;
        } else {
            BlockPos other = (BlockPos) otherObj;
            if (this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ()) {
                if (other instanceof WorldPos)
                    return ((WorldPos) other).getWorld() == this.getWorld();
                else // NOTE: This does not compare the face of each coordinate
                    return true;
            }
            return false;
        }
    }

//    public SigBlock getSigBlock() {
//        return new SigBlock(getBlock(), getState());
//    }

    // Simple wrapper method for getBlockID()
    public Block getBlock() {
//        return this.getWorld().getBlock(this.getX(), this.getY(), this.getZ());
        return this.getWorld().getBlockState(new BlockPos(getX(), getY(), getX())).getBlock();
    }

    // Sister function to getBlockID() for meta values.
//    public int getMetaId() {
//        return getWorld().getBlockMetadata(getX(), getY(), getZ());
//    }
    
    public IBlockState getState() {
        return getWorld().getBlockState(new BlockPos(getX(), getY(), getZ()));
    }

    /**
     * Simple wrapper method for setBlockID()
     * 
     * @param blockID
     * @return true if successful
     */
    public boolean setBlockIdAndUpdate(Block block) {
        if (block == Blocks.bedrock || getBlock() == Blocks.bedrock)
            return false; // You cannot delete or place bedrock
//        return this.getWorld().setBlock(posX, posY, posZ, blockID);
        return this.getWorld().setBlockState(new BlockPos(getX(), getY(), getZ()), block.getDefaultState());
    }

    public boolean setBlockState(SigBlock sig) {
        if (sig.equals(Blocks.bedrock) || getBlock() == Blocks.bedrock)
            return false; // You cannot delete or place bedrock
//        return this.getWorld().setBlock(getX(), get, posZ, sig.blockID, sig.meta, 2);
        return this.getWorld().setBlockState(new BlockPos(getX(), getY(), getZ()), (IBlockState) sig.blockID);
        // NOTE: Use last arg 3 if you want a block update.
    }

    public boolean setBlockState(IBlockState state) {
        if (state.getBlock() == Blocks.bedrock || getBlock() == Blocks.bedrock)
            return false; // You cannot delete or place bedrock
//        return this.getWorld().setBlock(posX, posY, posZ, blockID, meta, 3);
        return this.getWorld().setBlockState(new BlockPos(getX(), getX(), getZ()), state);//TODO: FIX THIS
    }

    public String toString() {// this is designed to match the GSON output
        return "{\"dimensionID\":" + dimensionID + ",\"face\":" + face + ",\"posX\":" + getX() + ",\"posY\":" + getY() + ",\"posZ\":" + getZ() + "}";
        // return "(" + posX + "," + posY + "," + posZ + ")";
    }

    public ArrayList<WorldPos> getDirectNeighbors() {
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        // 6 cardinal sides
        neighbors.add(offset(0, 1, 0));
        neighbors.add(offset(0, -1, 0));
        neighbors.add(offset(0, 0, -1));
        neighbors.add(offset(1, 0, 0));
        neighbors.add(offset(0, 0, 1));
        neighbors.add(offset(-1, 0, 0));
        return neighbors;
    }

    public ArrayList<WorldPos> getNeighbors() {
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        // 6 cardinal sides
        neighbors.add(offset(0, 1, 0));
        neighbors.add(offset(0, -1, 0));
        neighbors.add(offset(0, 0, -1));
        neighbors.add(offset(1, 0, 0));
        neighbors.add(offset(0, 0, 1));
        neighbors.add(offset(-1, 0, 0));

        // 12 edge diagonals
        // Josiah: If there was a way to get Build Master and Runecraft to
        // cooperate without these
        // extra 12 checks I would really rather only do 1/3 the workload when
        // loading large Runecraft
        // structures
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
        // the 8 corner diagonals are not included
        return neighbors;
    }

    public ArrayList<WorldPos> getNeighbors(Vector3 orientation) {
        int x = 0;
        int y = 0;
        int z = 0;
        ArrayList<WorldPos> neighbors = new ArrayList<WorldPos>();
        if (Math.abs(orientation.x) == 1) {
            for (z = -1; z <= 1; ++z) {
                for (y = -1; y <= 1; ++y) {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        if (Math.abs(orientation.y) == 1) {
            for (z = -1; z <= 1; ++z) {
                for (x = -1; x <= 1; ++x) {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        if (Math.abs(orientation.z) == 1) {
            for (y = -1; y <= 1; ++y) {
                for (x = -1; x <= 1; ++x) {
                    neighbors.add(offset(x, y, z));
                }
            }
            return neighbors;
        }
        return getNeighbors();
    }

    public double getDistance(WorldPos other) {
        double xzDist_2 = (getX() - other.getX()) * (getX() - other.getX()) + (getZ() - other.getZ()) * (getZ() - other.getZ());// Math.sqrt(
        return Math.sqrt(xzDist_2 + (getY() - other.getY()) * (getY() - other.getY()));
    }

    public boolean isSolid() {
        Material base = getBlock().getMaterial();
        return base.isSolid();
    }

    public void bump(Vector3 vec) {
        bump(vec.x, vec.y, vec.z);
    }

    public boolean isCrushable() {
        return Tiers.isCrushable(getBlock());
    }

}
