package com.codetaylor.mc.pyrotech.modules.pyrotech.compat.crafttweaker;

import com.codetaylor.mc.athenaeum.integration.crafttweaker.mtlib.helpers.CTLogHelper;
import com.codetaylor.mc.pyrotech.modules.pyrotech.ModulePyrotechRegistries;
import com.codetaylor.mc.pyrotech.modules.pyrotech.recipe.OvenStoneRecipe;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.pyrotech.StoneOven")
public class ZenOvenStone {

  @ZenMethod
  public static void removeRecipes(IIngredient output) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(output)));
  }

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input)
    ));
  }

  @ZenMethod
  public static void blacklistSmeltingRecipe(IIngredient[] output) {

    CraftTweaker.LATE_ACTIONS.add(new IAction() {

      @Override
      public void apply() {

        for (IIngredient ingredient : output) {
          OvenStoneRecipe.blacklistSmeltingRecipe(CraftTweakerMC.getIngredient(ingredient));
        }
      }

      @Override
      public String describe() {

        return "Blacklisting smelting recipes from stone oven: " + CTLogHelper.getStackDescription(output);
      }
    });
  }

  @ZenMethod
  public static void whitelistSmeltingRecipe(IIngredient[] output) {

    CraftTweaker.LATE_ACTIONS.add(new IAction() {

      @Override
      public void apply() {

        for (IIngredient ingredient : output) {
          OvenStoneRecipe.whitelistSmeltingRecipe(CraftTweakerMC.getIngredient(ingredient));
        }
      }

      @Override
      public String describe() {

        return "Whitelisting smelting recipes from stone oven: " + CTLogHelper.getStackDescription(output);
      }
    });
  }

  public static class RemoveRecipe
      implements IAction {

    private final Ingredient output;

    public RemoveRecipe(Ingredient output) {

      this.output = output;
    }

    @Override
    public void apply() {

      OvenStoneRecipe.removeRecipes(this.output);
    }

    @Override
    public String describe() {

      return "Removing stone oven recipes for " + this.output;
    }
  }

  public static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;

    public AddRecipe(
        String name,
        ItemStack output,
        Ingredient input
    ) {

      this.name = name;
      this.input = input;
      this.output = output;
    }

    @Override
    public void apply() {

      OvenStoneRecipe recipe = new OvenStoneRecipe(
          this.output,
          this.input
      );
      ModulePyrotechRegistries.OVEN_STONE_RECIPE.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding stone oven recipe for " + this.output;
    }
  }

}
