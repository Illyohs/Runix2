package us.illyohs.runix2.api.rune;

import net.minecraft.block.Block;

/**
 * Created by illyohs on 12/25/15.
 */
public abstract class BaseRune {

    protected String    name;
    protected boolean   isRuneFlat;


    public BaseRune(String name, boolean isFlat) {
        this.name = name;
        this.isRuneFlat = isFlat;
    }

    protected abstract Block[][][] runicTemplateOriginal();


    public String getName() {
        return "rune." + name;
    }

    public String getUnlocalizedName() {
        return "rune." + this.name;
    }

    public boolean isRuneFlat() {
        return isRuneFlat;
    }
}
