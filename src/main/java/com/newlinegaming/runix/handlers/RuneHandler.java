package com.newlinegaming.runix.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.newlinegaming.runix.AbstractRune;
import com.newlinegaming.runix.PersistentRune;
import com.newlinegaming.runix.Vector3;
import com.newlinegaming.runix.WorldPos;
import com.newlinegaming.runix.helper.LogHelper;
import com.newlinegaming.runix.rune.BuildMasterRune;
import com.newlinegaming.runix.rune.CompassRune;
import com.newlinegaming.runix.rune.ElevatorRune;
import com.newlinegaming.runix.rune.FaithRune;
import com.newlinegaming.runix.rune.FerrousWheelRune;
import com.newlinegaming.runix.rune.FtpRune;
import com.newlinegaming.runix.rune.GreekFireRune;
import com.newlinegaming.runix.rune.HoarFrostRune;
import com.newlinegaming.runix.rune.OracleRune;
import com.newlinegaming.runix.rune.RunecraftRune;
import com.newlinegaming.runix.rune.TeleporterRune;
import com.newlinegaming.runix.rune.TorchBearerRune;
import com.newlinegaming.runix.rune.WaypointRune;
import com.newlinegaming.runix.rune.ZeerixChestRune;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * RuneHandler is the main switchboard between all runes. It contains
 * runeRegistry, which is the list of all runes to scan for matches and be
 * executed. It is a singleton and so RuneHandler.getInsance() is a good way to
 * jump back to a global context.
 * 
 * It should not contain any code specific to a single rune. runes that depend
 * on each other such as Teleporter and Waypoint should use each other's static
 * activeMagic list instead of going through RuneHandler. Generic open-ended
 * interaction such as moveMagic() are handled through RuneHandler, since there
 * is no telling how many runes it could affect.
 */
public class RuneHandler {
    private static RuneHandler     instance     = null;                         // Singleton
                                                                                // pattern
    public ArrayList<AbstractRune> runeRegistry = new ArrayList<AbstractRune>();

    private RuneHandler() {
        // TODO: Make a wrappper class for adding runes something alone the
        // lines of RuneHandler.addRune(RuneFooRune), or add it to a Runix

        // runeRegistry.add(new PlayerHandler());
        runeRegistry.add(new WaypointRune());
        runeRegistry.add(new FaithRune());
        runeRegistry.add(new CompassRune());
        runeRegistry.add(new FtpRune());
        runeRegistry.add(new TeleporterRune());
        runeRegistry.add(new RunecraftRune()); // FIXME: Make Runecraft runes
                                               // respect TileEntity Inventories
        // runeRegistry.add(new RubricRune());
        runeRegistry.add(new TorchBearerRune());
        runeRegistry.add(new ZeerixChestRune());
        runeRegistry.add(new FerrousWheelRune());
        runeRegistry.add(new OracleRune());
        runeRegistry.add(new GreekFireRune());
        runeRegistry.add(new HoarFrostRune());
        // runeRegistry.add(new DomainRune());
        // runeRegistry.add(new LightBeamRune());
        runeRegistry.add(new ElevatorRune());
        runeRegistry.add(new BuildMasterRune());
    }

    public void addRune(AbstractRune rune) {
        runeRegistry.add(rune);
    }

    public static RuneHandler getInstance() {
        if (instance == null)
            instance = new RuneHandler();
        return instance;
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.entityPlayer.worldObj.isRemote)// runes server side only
            return;
        // Note: I've noticed that torch RIGHT_CLICK when you can't place a
        // torch only show up client side, not server side
        if (!event.entityPlayer.worldObj.isRemote && event.action == Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) {
            possibleRuneActivationEvent(event.entityPlayer, new WorldPos(event.entityPlayer.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ(), event.face));
        }
    }

    @SubscribeEvent
    public void saving(Save saveEvent) {
        if (saveEvent.world.provider.getDimensionId() == 0 && !saveEvent.world.isRemote)
            // Josiah: I figure it's likely there's only one of these
            for (AbstractRune r : runeRegistry)
                if (r instanceof PersistentRune)
                    ((PersistentRune) r).saveActiveRunes(saveEvent);
    }

    @SubscribeEvent
    public void loadServer(Load loadEvent) {
        if (loadEvent.world.provider.getDimensionId() == 0 && !loadEvent.world.isRemote)
            for (AbstractRune r : runeRegistry)
                if (r instanceof PersistentRune)
                    ((PersistentRune) r).loadRunes(loadEvent);
    }

    @SubscribeEvent
    public void playerLogin(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer) { //fires once each for Client and Server side join event
            for(AbstractRune r : runeRegistry) {
                if(r instanceof PersistentRune)
                    ((PersistentRune)r).onPlayerLogin(event.entity.getName());
            }
//                    ((PersistentRune) r).onPlayerLogin(((EntityPlayer)event.entity).getDisplayName());         
        }
    }

    /**
     * Detects a rune pattern, and executes it.
     */
    @SuppressWarnings("static-access")
    public void possibleRuneActivationEvent(EntityPlayer player, WorldPos coords) {
        Pair<AbstractRune, Vector3> matchingRuneInfo = checkForAnyRunePattern(coords);
        // TODO: check for Activator Rail in hand and subscribe the rune to
        // minecart events
        if (matchingRuneInfo != null) {
            AbstractRune matchingRune = matchingRuneInfo.getLeft();
            String direction;
            if (matchingRune.isAssymetrical())
                direction = Vector3.faceString[Arrays.asList(Vector3.facing).indexOf(matchingRuneInfo.getRight())];
            else
                direction = Vector3.faceString[coords.face];
            matchingRune.aetherSay(player, "The Aether sees you activating a " + EnumChatFormatting.GREEN + matchingRune.getRuneName() + EnumChatFormatting.WHITE + " facing "
                    + direction + " at " + coords.getX() + "," + coords.getY() + "," + coords.getZ() + ".");

            LogHelper.info(player.getDisplayName() + " Has activated a " + matchingRune.getRuneName() + "");
            matchingRune.execute(coords, player, matchingRuneInfo.getRight());
        }
    }

    /**
     * This is the main switch board between all of the runes. It iterates
     * through all runes in the order that they are registered and asks if each
     * one matches the pattern of blocks at the coordinates.
     * 
     * @param coords
     *            location of the right click
     * @return AbstractRune class if there is a match, null otherwise
     */
    private Pair<AbstractRune, Vector3> checkForAnyRunePattern(WorldPos coords) {
        for (AbstractRune aRuneRegistry : runeRegistry) {
            WorldPos result = aRuneRegistry.checkRunePattern(new WorldPos(coords));
            if (result != null) {
                Vector3 forward = Vector3.facing[result.face];// result can
                                                              // contain facing
                                                              // information for
                                                              // assymetrical
                                                              // runes
                return new MutablePair<AbstractRune, Vector3>(aRuneRegistry, forward);
            }
        }
        return null;
    }

    public void moveMagic(HashMap<WorldPos, WorldPos> positionsMoved) {
        for (AbstractRune rune : runeRegistry) {
            rune.moveMagic(positionsMoved);
        }
    }

    /**
     * This is modeled after conductanceStep() but on a macro level. Recursive
     * chaining of rune structures is now working. You can FTP a Runecraft that
     * is touching a Faith block and the whole island will be treated and moved
     * as one structure. param authority
     */
    public HashSet<WorldPos> chainAttachedStructures(HashSet<WorldPos> structure, AbstractRune originator) {
        HashSet<WorldPos> activeEdge;
        HashSet<WorldPos> nextEdge = new HashSet<WorldPos>(structure);// starts
                                                                      // off
                                                                      // being a
                                                                      // copy of
                                                                      // structure

        while (!nextEdge.isEmpty() && structure.size() < 500000) {
            activeEdge = nextEdge;
            nextEdge = new HashSet<WorldPos>();

            for (AbstractRune rune : runeRegistry) {
                if (rune instanceof PersistentRune) {
                    // pass in and side-effect the collection
                    HashSet<WorldPos> additionalBlocks = new HashSet<WorldPos>();
                    for (PersistentRune pRune : ((PersistentRune) rune).getActiveMagic()) {
                        if (activeEdge.contains(pRune.location)) {
                            if ((originator.authority() == 0 || originator.authority() > pRune.authority()) && originator != pRune) {
                                // FaithRune is the only authority user at the
                                // moment
                                additionalBlocks.addAll(pRune.fullStructure());
                                additionalBlocks.removeAll(structure); // we
                                                                       // only
                                                                       // want
                                                                       // new
                                                                       // blocks
                            } else if (pRune instanceof FaithRune && originator != pRune) { // obviously
                                                                                            // don't
                                                                                            // block
                                                                                            // yourself
                                // ensure Faith Anchor stays where it is, even
                                // if other blocks are moved
                                structure.remove(pRune.location);
                                activeEdge.remove(pRune.location);
                                additionalBlocks.remove(pRune.location);
                            }
                        }
                    }
                    structure.addAll(additionalBlocks);
                    nextEdge.addAll(additionalBlocks);
                }
            }
        }

        if (nextEdge.size() != 0)// tear detection: this should be empty by the
                                 // last step
            System.err.println("RunixMain exceeded maximum structure chaining size: " + structure.size() + " blocks.");
        return structure;
    }

    // TODO public JSON extractMagic(Collection<WorldXYZ> blocks)

    public ArrayList<PersistentRune> getAllRunesByPlayer(EntityPlayer player) {
        ArrayList<PersistentRune> playerRunes = new ArrayList<PersistentRune>();
        for (AbstractRune r : runeRegistry)
            if (r instanceof PersistentRune) {
                // TODO change getRuneByPlayer to return list when
                // oneRunePerPerson = false.
                PersistentRune rune = ((PersistentRune) r).getRuneByPlayer(player);
                if (rune != null)
                    playerRunes.add(rune);
            }
        return playerRunes;
    }

}