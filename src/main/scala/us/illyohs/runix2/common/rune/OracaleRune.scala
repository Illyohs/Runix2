package us.illyohs.runix2.common.rune

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

import us.illyohs.runix2.api.Runix2API
import us.illyohs.runix2.api.rune.Rune._

object OracaleRune
  extends BaseRune("oracle", true) {

  override def pattern(): Array[Array[Array[Block]]] = {
    val RED = Blocks.REDSTONE_WIRE
    Array(Array(
      Array(RED,RED,RED),
      Array(RED,TIER,RED),
      Array(RED,RED,RED)
    ))
  }

  override def execute(world: World, pos: BlockPos, player: EntityPlayer): Unit = {
//    if (player.getHeldItem(EnumHand.MAIN_HAND).getItem == Items.GOLDEN_SWORD) {
//      world.getBlockState(pos)
//    }
    val block = world.getBlockState(pos).getBlock
    player.addChatComponentMessage(new TextComponentString(block.getUnlocalizedName + "has " +
      Runix2API.getEnergyFromBlock(block) + " Energy"))
  }
}
