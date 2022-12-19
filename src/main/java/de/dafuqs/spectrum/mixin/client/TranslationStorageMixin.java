package de.dafuqs.spectrum.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

    @Mutable
    @Shadow
    @Final
    private Map<String, String> translations;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addTranslations(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH) != Calendar.APRIL || calendar.get(Calendar.DAY_OF_MONTH) != 14) return;

        Map<String, String> builder = new HashMap<>(translations);
        builder.put("block.spectrum.crystallarieum", getCrystallarieuaeuieueum());

        this.translations = builder;
    }
    
    private static String getCrystallarieuaeuieueum() {
        List<String> possibilities = new ArrayList<>() {{
            add("Crystallarieum");
            add("Crystallerium");
            add("Crystallarium");
            add("Crystallium");
            add("Crystalleium");
            add("Crystallum");
            add("Crystallarieium");
            add("Christalerium");
        }};
        char c = MinecraftClient.getInstance().getSession().getUsername().toCharArray()[0];
        return possibilities.get((int) c % possibilities.size());
    }

}