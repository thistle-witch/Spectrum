package de.dafuqs.spectrum.items.energy;

import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.api.item.*;
import de.dafuqs.spectrum.api.render.*;
import de.dafuqs.spectrum.helpers.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.api.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CreativeInkAssortmentItem extends Item implements InkStorageItem<CreativeInkStorage>, CreativeOnlyItem, SlotBackgroundEffectProvider {
	
	public CreativeInkAssortmentItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(context.getBlockPos());
			if (blockEntity instanceof InkStorageBlockEntity<?> inkStorageBlockEntity) {
				inkStorageBlockEntity.getEnergyStorage().fillCompletely();
				inkStorageBlockEntity.setInkDirty();
				blockEntity.markDirty();
			}
		}
		return super.useOnBlock(context);
	}
	
	@Override
	public Drainability getDrainability() {
		return Drainability.ALWAYS;
	}
	
	@Override
	public CreativeInkStorage getEnergyStorage(ItemStack itemStack) {
		NbtCompound compound = itemStack.getNbt();
		if (compound != null && compound.contains("EnergyStore")) {
			return CreativeInkStorage.fromNbt(compound.getCompound("EnergyStore"));
		}
		return new CreativeInkStorage();
	}
	
	// Omitting this would crash outside the dev env o.O
	@Override
	public ItemStack getDefaultStack() {
		return super.getDefaultStack();
	}
	
	@Override
	public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		CreativeOnlyItem.appendTooltip(tooltip);
		getEnergyStorage(stack).addTooltip(tooltip);
	}
	
	@Override
	public SlotBackgroundEffectProvider.SlotEffect backgroundType(@Nullable PlayerEntity player, ItemStack stack) {
		return SlotEffect.BORDER_FADE;
	}
	
	@Override
	public int getBackgroundColor(@Nullable PlayerEntity player, ItemStack stack, float delta) {
		var colors = new ArrayList<InkColor>();
		
		if (player == null)
			return 0;
		
		var time = player.getWorld().getTime() % 864000;
		
		for (InkColor inkColor : SpectrumRegistries.INK_COLORS) {
			colors.add(inkColor);
		}
		
		if (colors.size() == 1) {
			var color = colors.get(0);
			return ColorHelper.colorVecToRGB(color.getColorVec());
		}
		
		var curColor = colors.get((int) (time % (30L * colors.size()) / 30));
		var nextColor = colors.get((int) ((time % (30L * colors.size()) / 30 + 1) % colors.size()));
		var blendFactor = (((float) time + delta) % 30) / 30F;
		
		return ColorHelper.interpolate(curColor.getTextColorVec(), nextColor.getTextColorVec(), blendFactor);
	}
}