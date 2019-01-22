package com.codetaylor.mc.pyrotech.modules.pyrotech.item;

import com.codetaylor.mc.athenaeum.util.BlockHelper;
import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.library.util.Util;
import com.codetaylor.mc.pyrotech.modules.pyrotech.init.ModuleBlocks;
import com.codetaylor.mc.pyrotech.modules.pyrotech.tile.TileBloom;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public abstract class ItemTongsFullBase
    extends Item {

  private final Supplier<ItemTongsBase> otherTongsSupplier;

  /* package */ ItemTongsFullBase(Supplier<ItemTongsBase> otherTongsSupplier, int durability) {

    this.otherTongsSupplier = otherTongsSupplier;

    this.setMaxStackSize(1);
    this.setMaxDamage(durability);
  }

  /**
   * Returns the empty version of the full tongs passed in, with damage. If the
   * tongs are destroyed as a result of the damage, an empty itemstack is
   * returned instead.
   * <p>
   * Does not modify input stack.
   *
   * @param toEmpty the full tongs itemstack
   * @return the empty version of the full tongs passed in
   */
  public static ItemStack getEmptyItemStack(ItemStack toEmpty) {

    NBTTagCompound tagCompound = toEmpty.getTagCompound();

    if (tagCompound == null) {
      return toEmpty;
    }

    Item item = toEmpty.getItem();

    if (!(item instanceof ItemTongsFullBase)) {
      return toEmpty;
    }

    if (toEmpty.attemptDamageItem(((ItemTongsFullBase) item).getDamagePerUse(), RandomHelper.random(), null)) {
      return ItemStack.EMPTY;
    }

    ItemTongsFullBase tongs = (ItemTongsFullBase) item;

    ItemStack itemStack = new ItemStack(tongs.otherTongsSupplier.get(), 1, toEmpty.getMetadata());
    NBTTagCompound copy = tagCompound.copy();
    copy.removeTag(StackHelper.BLOCK_ENTITY_TAG);
    itemStack.setTagCompound(copy);

    return itemStack;
  }

  @Override
  public boolean isEnchantable(@Nonnull ItemStack stack) {

    return false;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

    ItemStack heldItem = player.getHeldItem(hand);

    if (hand != EnumHand.MAIN_HAND
        || heldItem.getItem() != this) {
      return ActionResult.newResult(EnumActionResult.FAIL, heldItem);
    }

    RayTraceResult target = this.rayTrace(world, player, false);

    if (target.typeOfHit != RayTraceResult.Type.BLOCK) {
      return ActionResult.newResult(EnumActionResult.FAIL, heldItem);
    }

    return this.onItemRightClick(world, player, heldItem, target);
  }

  private ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, ItemStack heldItem, RayTraceResult target) {

    EnumFacing sideHit = target.sideHit;
    BlockPos pos = target.getBlockPos();
    BlockPos offset = pos.offset(sideHit);
    NBTTagCompound tagCompound = heldItem.getTagCompound();

    if (ModuleBlocks.BLOOM.canPlaceBlockAt(world, offset)
        && tagCompound != null) {

      if (!world.isRemote) {
        world.setBlockState(offset, ModuleBlocks.BLOOM.getDefaultState());
        TileBloom tile = (TileBloom) world.getTileEntity(offset);

        if (tile != null) {
          BlockPos tilePos = tile.getPos();
          tile.readFromNBT(tagCompound.getCompoundTag(StackHelper.BLOCK_ENTITY_TAG));
          tile.setPos(tilePos);
          BlockHelper.notifyBlockUpdate(world, offset);
        }
      }

      ItemStack itemStack = ItemTongsFullBase.getEmptyItemStack(heldItem);

      if (itemStack.isEmpty()) {

        if (!world.isRemote) {
          world.playSound(
              null,
              player.getPosition(),
              SoundEvents.ENTITY_ITEM_BREAK,
              SoundCategory.BLOCKS,
              1.0F,
              Util.RANDOM.nextFloat() * 0.4F + 0.8F
          );
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, ItemStack.EMPTY);
      }

      return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
    }

    return ActionResult.newResult(EnumActionResult.PASS, heldItem);
  }

  protected abstract int getDamagePerUse();

  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {

    if (stack.getItem() != this) {
      return;
    }

    NBTTagCompound tagCompound = stack.getTagCompound();

    if (tagCompound != null
        && tagCompound.hasKey(StackHelper.BLOCK_ENTITY_TAG)) {

      NBTTagCompound teCompound = tagCompound.getCompoundTag(StackHelper.BLOCK_ENTITY_TAG);
      String langKey = teCompound.getString("langKey") + ".name";

      if (I18n.canTranslate(langKey)) {
        String translatedLangKey = I18n.translateToLocal(langKey);
        String translatedTooltip = I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".unique.name", translatedLangKey).trim();

        if (tooltip.size() > 1) {
          tooltip.add(1, TextFormatting.DARK_RED + translatedTooltip + TextFormatting.RESET);

        } else {
          tooltip.add(TextFormatting.GOLD + translatedTooltip + TextFormatting.RESET);
        }
      }
    }
  }
}