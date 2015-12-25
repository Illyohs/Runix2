package us.illyohs.runix2.api.rune;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by illyohs on 12/25/15.
 */
@SideOnly(Side.CLIENT)
public interface ICustomRuneRender {

    void renderRune(double x, double y, double z, int tick);
}
