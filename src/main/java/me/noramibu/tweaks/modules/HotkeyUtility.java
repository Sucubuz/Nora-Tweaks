package me.noramibu.tweaks.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.noramibu.tweaks.NoraTweaks;
import me.noramibu.tweaks.gui.screens.HotkeysScreen;
import me.noramibu.tweaks.util.Hotkey;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.utils.misc.input.KeyAction.Press;

public class HotkeyUtility extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final List<Hotkey> hotkeys = new ArrayList<>();

    public HotkeyUtility() {
        super(NoraTweaks.CATEGORY, "hotkey-utility", "Allows you to set key combinations to switch to a specific hotbar slot.");
        loadHotkeys();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WButton button = theme.button("Configure Hotkeys");
        button.action = () -> mc.setScreen(new HotkeysScreen(theme, this));
        return button;
    }

    public void saveHotkeys() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray array = new JsonArray();
        for (Hotkey hotkey : hotkeys) {
            array.add(hotkey.toJson());
        }

        try (FileWriter writer = new FileWriter(getHotkeysFile())) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHotkeys() {
        hotkeys.clear();
        File file = getHotkeysFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                JsonArray array = gson.fromJson(reader, JsonArray.class);
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        hotkeys.add(Hotkey.fromJson(array.get(i).getAsJsonObject()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getHotkeysFile() {
        File folder = new File(MeteorClient.FOLDER, "nora-tweaks");
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, "hotkeys.json");
    }

    @Override
    public void onDeactivate() {
        for (Hotkey hotkey : hotkeys) {
            hotkey.resetState();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        for (Hotkey hotkey : hotkeys) {
            if (hotkey.delayLeft > 0) {
                hotkey.delayLeft--;
            } else {
                hotkey.pressCount = 0;
            }
        }
    }

    @EventHandler
    private void onKeyPress(KeyEvent event) {
        if (event.action != Press) return;

        for (Hotkey hotkey : hotkeys) {
            if (hotkey.keybind.matches(true, event.key, event.modifiers)) {
                if (hotkey.delayLeft > 0) {
                    hotkey.pressCount++;
                } else {
                    hotkey.pressCount = 1;
                }

                hotkey.delayLeft = hotkey.pressDelay;

                if (hotkey.pressCount >= hotkey.presses) {
                    switch (hotkey.action) {
                        case SwitchSlot:
                            int targetSlot = hotkey.slot - 1;
                            if (mc.player != null && mc.player.getInventory().getSelectedSlot() != targetSlot) {
                                mc.player.getInventory().setSelectedSlot(targetSlot);
                            }
                            break;
                        case HoldItem:
                            // TODO: Implement inventory search and hold logic
                            break;
                    }
                    hotkey.resetState();
                }
            }
        }
    }
} 