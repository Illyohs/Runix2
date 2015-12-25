package com.newlinegaming.runix.rune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.newlinegaming.runix.AbstractTimedRune;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.WorldPos;
import com.newlinegaming.runix.helper.LogHelper;
import com.newlinegaming.runix.helper.RenderHelper;
import com.newlinegaming.runix.utils.Util_Movement;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RunecraftRune extends AbstractTimedRune {

    protected static ArrayList<PersistentRune> activeMagic        = new ArrayList<PersistentRune>();
    private HashSet<WorldPos>                  vehicleBlocks      = new HashSet<WorldPos>();
    private transient RenderHelper             renderer           = null;
    private boolean                            moveInProgress     = false;
    private boolean                            snaggedOnSomething = false;
    protected boolean                          buttonMode         = true;

    public RunecraftRune() {
        runeName = "Runecraft";
    }

    /**
     * Runecraft Runix Vehicle blocks track with a player while active. Toggle
     * it by right clicking the center block. You can jump up to travel up,
     * sneak to go down.
     * 
     * @param coords
     *            Center rune block that the vehicle is checked from
     * @param player2
     *            Person that the vehicle gloms on to
     */
    public RunecraftRune(WorldPos coords, EntityPlayer player2) {
        super(coords, player2, "Runecraft");
        setPlayer(null); // this is because poke() acts as if the Rune was
                         // activated a second time when it is first constructed
        this.runeName = "Runecraft";
        usesConductance = true;
    }

    /**
     * initializeRune() is necessary because of a circular condition in the
     * event registry that does not play well with the GSON object constructor
     * loading from loadRunes()
     */
    protected void initializeRune() {
        renderer = new RenderHelper();
        updateEveryXTicks(4);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Block[][][] runicTemplateOriginal() {
        Block GOLD = Blocks.gold_ore;
        return new Block[][][] { { { TIER, GOLD, TIER }, { GOLD, FUEL, GOLD }, { TIER, GOLD, TIER }

                } };
    }

    protected WorldPos getDestinationByPlayer(EntityPlayer subject) {
        if (getPlayer() != null && subject.equals(getPlayer())) {
            int dX = (int) (getPlayer().posX - location.getX() - .5);
            int dY = (int) (getPlayer().posY - location.getY() - 1);
            int dZ = (int) (getPlayer().posZ - location.getZ() - .5);
            if (6.0 < location.getDistance(new WorldPos(getPlayer()))) {
                setPlayer(null); // Vehicle has been abandoned
                aetherSay(subject, "Runecraft has been abandoned.");
                return location;
            } else {
                if (getPlayer().isSneaking())
                    dY -= 1;
                return location.offset(dX, dY, dZ);
            }
        }
        return location;
    }

    @Override
    protected void onUpdateTick(EntityPlayer subject) {
        if (moveInProgress) {
            return;
        }
        if (checkRunePattern(location) != null) { // rune is still intact
            try {
                moveInProgress = true;
                WorldPos destination = getDestinationByPlayer(subject);
                if (!location.equals(destination)) {
                    HashMap<WorldPos, WorldPos> move = Util_Movement.displaceShape(vehicleBlocks, location, destination);
                    if (!Util_Movement.shapeCollides(move)) {
                        snaggedOnSomething = false;
                        vehicleBlocks = Util_Movement.performMove(move);// Josiah:
                                                                        // it
                                                                        // turns
                                                                        // out
                                                                        // that
                                                                        // running
                                                                        // out
                                                                        // of
                                                                        // gas
                                                                        // isn't
                                                                        // fun
                    } else { // collision
                        if (snaggedOnSomething == false) { // this is to avoid
                                                           // chat spam, it only
                                                           // says it once
                            aetherSay(getPlayer(), "Runecraft collision!");
                            snaggedOnSomething = true;
                        }
                    }
                }
                moveInProgress = false;
            } catch (Throwable t) { // this is necessary because otherwise
                                    // moveInProgress can get in an inconsistent
                                    // state
                LogHelper.fatal("Runecraft failed.");
                LogHelper.fatal(t);
            }
        } else { // getPlayer() == null
            setPlayer(null); // clears the UUID and disables the rune
            disabled = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderWireframe(RenderWorldLastEvent evt) {
        if (getPlayer() != null)
            if (!renderer.highlightBoxes(vehicleBlocks, disabled, getPlayer())) {
                if (disabled)
                    setPlayer(null); // done with closing animation
            }
    }

    @SubscribeEvent
    public void event(BreakEvent b) {

    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (getPlayer() != null && event.action == Action.LEFT_CLICK_BLOCK)
            if (event.isCancelable()) {
                WorldPos punchBlock = new WorldPos(event.entity.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ());
                if (vehicleBlocks.contains(punchBlock)) {
                    if (location.getDistance(punchBlock) < 3) {
                        boolean counterClockwise = !Util_Movement.lookingRightOfCenterBlock(getPlayer(), location);
                        HashMap<WorldPos, WorldPos> move = Util_Movement.xzRotation(vehicleBlocks, location, counterClockwise);
                        if (!Util_Movement.shapeCollides(move))
                            vehicleBlocks = Util_Movement.performMove(move);
                    }
                    event.setCanceled(true); // build protect anything in
                                             // vehicleBlocks
                    System.out.println("Runecraft protected");
                }
            }
    }

    @Override
    protected void poke(EntityPlayer poker, WorldPos coords) {
        if (renderer == null) // initialization on the first time the rune is
                              // poked
            initializeRune();

        if (getPlayer() != null) {
            setPlayer(null); /// disabled = true; //player will not be set to
                             /// null until the closing animation completes
            aetherSay(poker, "You are now free from the Runecraft.");
        } else {
            setPlayer(poker); // assign a player and start
            aetherSay(poker, "The Runecraft is now locked to your body.");
            usesConductance = true; // backwards compatibility
            HashSet<WorldPos> newVehicleShape = attachedStructureShape(poker);
            if (newVehicleShape.isEmpty()) {
                vehicleBlocks = removeAirXYZ(vehicleBlocks);
            } else {
                vehicleBlocks = newVehicleShape;
                renderer.reset();
            }
        }
    }

    /**
     * Removes the coordinates of any air blocks from the shape Set. This can
     * break contiguous structures and actually return a non-contiguous
     * structure. For Runecraft, this is desirable.
     */
    private HashSet<WorldPos> removeAirXYZ(HashSet<WorldPos> oldShapeCoords) {
        for (Iterator<WorldPos> i = oldShapeCoords.iterator(); i.hasNext();) {
            WorldPos xyz = i.next(); // an iterator is necessary here because of
                                     // ConcurrentModificationException
            if (xyz.getBlock() == Blocks.air) // We specifically want to exclude
                                              // AIR to avoid confusing
                                              // collisions
                i.remove();
        }

        return oldShapeCoords;
    }

    /**
     * Runecraft lives or dies by its player. So the PersistentRune behavior
     * needs to be augmented with a 'disabled' switch.
     */
    public void setPlayer(EntityPlayer playerObj) {
        super.setPlayer(playerObj);
        if (getPlayer() != null)
            disabled = false;
        else
            disabled = true;
    }

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return activeMagic;
    }

    @Override
    public boolean oneRunePerPerson() {
        return true;
    }

    public boolean isFlatRuneOnly() {
        return false;
    }
}