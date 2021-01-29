package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.items.SpectrumItems;
import de.dafuqs.spectrum.recipe.AltarCraftingRecipe;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class AltarCraftingScreenHandler extends AbstractRecipeScreenHandler<Inventory> {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeBookCategory category;

    public AltarCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(SpectrumScreenHandlerTypes.ALTAR, SpectrumRecipeTypes.ALTAR, RecipeBookCategory.CRAFTING, syncId, playerInventory);
    }

    protected AltarCraftingScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AltarCraftingRecipe> recipeType, RecipeBookCategory recipeBookCategory, int i, PlayerInventory playerInventory) {
        this(type, recipeType, recipeBookCategory, i, playerInventory, new SimpleInventory(15), new ArrayPropertyDelegate(2));
    }

    public AltarCraftingScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        this(SpectrumScreenHandlerTypes.ALTAR, SpectrumRecipeTypes.ALTAR, RecipeBookCategory.CRAFTING, syncId, playerInventory, inventory, propertyDelegate);
    }

    protected AltarCraftingScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AltarCraftingRecipe> recipeType, RecipeBookCategory recipeBookCategory, int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, i);
        this.category = recipeBookCategory;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;

        checkSize(inventory, 9+5);
        checkDataCount(propertyDelegate, 2);

        // crafting slots
        int m;
        int n;
        for(m = 0; m < 3; ++m) {
            for(n = 0; n < 3; ++n) {
                this.addSlot(new Slot(inventory, n + m * 3, 30 + n * 18, 19 + m * 18));
            }
        }

        // gem slots
        this.addSlot(new StackFilterSlot(inventory, 9,  44 + 0 * 18, 77, Items.AMETHYST_SHARD));
        this.addSlot(new StackFilterSlot(inventory, 10, 44 + 1 * 18, 77, SpectrumItems.CITRINE_SHARD_ITEM));
        this.addSlot(new StackFilterSlot(inventory, 11, 44 + 2 * 18, 77, SpectrumItems.TOPAZ_SHARD_ITEM));
        this.addSlot(new StackFilterSlot(inventory, 12, 44 + 3 * 18, 77, SpectrumItems.ONYX_SHARD_ITEM));
        this.addSlot(new StackFilterSlot(inventory, 13, 44 + 4 * 18, 77, SpectrumItems.MOONSTONE_SHARD_ITEM));

        // preview slot
        this.addSlot(new ReadOnlySlot(inventory, 14, 124, 37));

        // player inventory
        int l;
        for(l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 112 + l * 18));
            }
        }

        // player hotbar
        for(l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 170));
        }

        this.addProperties(propertyDelegate);
    }

    public void populateRecipeFinder(RecipeFinder finder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
        }
    }

    public void clearCraftingSlots() {
        this.getSlot(0).setStack(ItemStack.EMPTY);
        this.getSlot(2).setStack(ItemStack.EMPTY);
    }

    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }

    public int getCraftingResultSlotIndex() {
        return 15;
    }

    public int getCraftingWidth() {
        return 3;
    }

    public int getCraftingHeight() {
        return 3;
    }

    public int getCraftingSlotCount() {
        return 9;
    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    /**
     * Handle Shift-Clicking
     * TODO
     */
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 1 && index != 0) {
                if (index >= 3 && index < 30) {
                    if (!this.insertItem(itemStack2, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Environment(EnvType.CLIENT)
    public int getCookProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }

    @Environment(EnvType.CLIENT)
    public RecipeBookCategory getCategory() {
        return this.category;
    }

    public boolean method_32339(int i) {
        return i != 1;
    }

    /*public Text getTitle() {
        return new TranslatableText("block.spectrum.altar");
    }*/
}
