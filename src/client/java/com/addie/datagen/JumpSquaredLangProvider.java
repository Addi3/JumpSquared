package com.addie.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class JumpSquaredLangProvider extends FabricLanguageProvider {

    public JumpSquaredLangProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateTranslations(TranslationBuilder translations) {

        translations.add("key.jumpmod.toggle", "Toggle Jump Mod");
        translations.add("category.jumpmod", "Jump Mod");
    }
}
