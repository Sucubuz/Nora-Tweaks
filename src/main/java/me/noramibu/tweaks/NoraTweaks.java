package me.noramibu.tweaks;

import me.noramibu.tweaks.category.CustomCategoryManager;
import me.noramibu.tweaks.modules.AutoDirtPath;
import me.noramibu.tweaks.modules.CategoryManagerModule;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class NoraTweaks extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Nora Tweaks");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Nora Tweaks");

        CustomCategoryManager.init();

        // Modules
        Modules.get().add(new AutoDirtPath());
        Modules.get().add(new CategoryManagerModule());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "me.noramibu.tweaks";
    }
}
