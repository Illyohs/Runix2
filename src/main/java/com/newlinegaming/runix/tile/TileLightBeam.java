package com.newlinegaming.runix.tile;


import us.illyohs.libilly.block.tile.BaseTile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileLightBeam extends BaseTile {
    
    public TileLightBeam() {
        
    }
    
    @Override
    public void readFromModNBT(NBTTagCompound mNBT) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeToModNBT(NBTTagCompound mNBT) {
        // TODO Auto-generated method stub        
    }

    @Override
    public void onModDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        // TODO Auto-generated method stub
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
        
    @Override
    public void updateTile() {
        // TODO Auto-generated method stub    
    }
}
