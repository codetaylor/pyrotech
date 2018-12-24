package com.codetaylor.mc.pyrotech.modules.pyrotech.item;

import net.minecraft.item.Item;

public class ItemMillBlade
    extends Item {

  public ItemMillBlade(int maxUses) {

    this.setMaxStackSize(1);
    this.setMaxDamage(maxUses);
  }
}
