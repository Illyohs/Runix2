package com.newlinegaming.runix.rune;

import java.util.ArrayList;

import com.newlinegaming.runix.WorldPos;
import net.minecraft.block.Block;

import com.newlinegaming.runix.PersistentRune;

/**
 * PlayerHandler is a persistent rune because we would like to use the same saving and loading functionality.
 * It overrides checkRunePattern to ensure there is no way to create this rune in-game.
 * Instead a new PlayerHandler instance is created each time a new player enters the server for the first time.
 */
public class PlayerHandler extends PersistentRune {
    private static ArrayList<PersistentRune> serverPlayers = new ArrayList<PersistentRune>();

    @Override
    public WorldPos checkRunePattern(WorldPos coords) {
        return null;
    }

    @Override
    public ArrayList<PersistentRune> getActiveMagic() {
        return serverPlayers;
    }

    @Override
    public boolean oneRunePerPerson() {
        return true;
    }

    @Override
    protected Block[][][] runicTemplateOriginal() {
        return new Block[][][] {
        };
    }

    @Override
    public boolean isFlatRuneOnly() {
        return false;
    }

}
