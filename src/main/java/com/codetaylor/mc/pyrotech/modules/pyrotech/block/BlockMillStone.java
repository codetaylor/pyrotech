package com.codetaylor.mc.pyrotech.modules.pyrotech.block;

import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.pyrotech.library.util.Util;
import com.codetaylor.mc.pyrotech.modules.pyrotech.ModulePyrotechConfig;
import com.codetaylor.mc.pyrotech.modules.pyrotech.block.spi.BlockCombustionWorkerStoneBase;
import com.codetaylor.mc.pyrotech.modules.pyrotech.tile.TileMillStone;
import com.codetaylor.mc.pyrotech.modules.pyrotech.tile.TileMillStoneTop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockMillStone
    extends BlockCombustionWorkerStoneBase {

  public static final String NAME = "mill_stone";

  private static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(1.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0, 4.0 / 16.0, 15.0 / 16.0);

  @Override
  protected TileEntity createTileEntityTop() {

    return new TileMillStoneTop();
  }

  @Override
  protected TileEntity createTileEntityBottom() {

    return new TileMillStone();
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    if (this.isTop(state)) {
      return AABB_TOP;
    }

    return super.getBoundingBox(state, source, pos);
  }

  @Override
  public void onEntityWalk(World world, BlockPos pos, Entity entity) {

    if (this.isTop(world.getBlockState(pos))) {
      TileEntity tile = world.getTileEntity(pos.down());

      if (tile instanceof TileMillStone
          && ((TileMillStone) tile).workerIsActive()
          && ModulePyrotechConfig.STONE_MILL.ENTITY_DAMAGE_FROM_BLADE > 0) {
        entity.attackEntityFrom(DamageSource.GENERIC, ModulePyrotechConfig.STONE_MILL.ENTITY_DAMAGE_FROM_BLADE);
      }
    }

    super.onEntityWalk(world, pos, entity);
  }

  @Override
  protected void randomDisplayTickWorkingTop(IBlockState state, World world, BlockPos pos, Random rand) {

    double centerX = pos.getX();
    double centerY = pos.getY() - 0.2;
    double centerZ = pos.getZ();

    EnumFacing facing = state.getValue(Properties.FACING_HORIZONTAL);

    switch (facing) {

      case NORTH:
        centerX += 8.0 / 16.0;
        centerZ += 18.0 / 16.0;
        break;

      case SOUTH:
        centerX += 8.0 / 16.0;
        centerZ += -2.0 / 16.0;
        break;

      case EAST:
        centerX += -2.0 / 16.0;
        centerZ += 8.0 / 16.0;
        break;

      case WEST:
        centerX += 18.0 / 16.0;
        centerZ += 8.0 / 16.0;
        break;

    }

    world.spawnParticle(
        EnumParticleTypes.SMOKE_LARGE,
        centerX/* + (Util.RANDOM.nextDouble() - 0.5) * 0.25*/,
        centerY,
        centerZ/* + (Util.RANDOM.nextDouble() - 0.5) * 0.25*/,
        0,
        0.05 + (Util.RANDOM.nextFloat() * 2 - 1) * 0.05,
        0
    );
  }
}