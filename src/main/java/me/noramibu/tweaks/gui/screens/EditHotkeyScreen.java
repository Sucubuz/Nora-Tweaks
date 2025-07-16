package me.noramibu.tweaks.gui.screens;

import me.noramibu.tweaks.modules.HotkeyUtility;
import me.noramibu.tweaks.util.Hotkey;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ItemSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WItem;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.settings.ItemSetting;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EditHotkeyScreen extends WindowScreen {
    private final Hotkey hotkey;
    private final List<WKeybind> keybindWidgets = new ArrayList<>();
    private WTable actionSettingsTable;

    public EditHotkeyScreen(GuiTheme theme, HotkeyUtility module, Hotkey hotkey) {
        super(theme, "Edit Hotkey");
        this.hotkey = hotkey;
        if (this.parent == null) {
            this.parent = new HotkeysScreen(theme, module);
        }
    }

    @Override
    public void initWidgets() {
        keybindWidgets.clear();
        WTable table = add(theme.table()).expandX().widget();

        // Keybind, Presses, and Delay are always visible
        table.add(theme.label("Keybind:"));
        WKeybind keybindWidget = theme.keybind(hotkey.keybind);
        table.add(keybindWidget).expandX();
        keybindWidgets.add(keybindWidget);
        table.row();

        table.add(theme.label("Presses:"));
        WIntEdit pressesWidget = theme.intEdit(hotkey.presses, 1, 0, false);
        pressesWidget.action = () -> hotkey.presses = pressesWidget.get();
        table.add(pressesWidget).expandX();
        table.row();

        table.add(theme.label("Press Delay:"));
        WIntEdit pressDelayWidget = theme.intEdit(hotkey.pressDelay, 0, 0, false);
        pressDelayWidget.action = () -> hotkey.pressDelay = pressDelayWidget.get();
        table.add(pressDelayWidget).expandX();
        table.row();

        // Action dropdown
        table.add(theme.label("Action:"));
        WDropdown<Hotkey.Action> actionDropdown = theme.dropdown(hotkey.action);
        actionDropdown.action = () -> {
            hotkey.action = actionDropdown.get();
            buildActionSettings();
        };
        table.add(actionDropdown).expandX();
        table.row();

        // Table for action-specific settings
        actionSettingsTable = add(theme.table()).expandX().widget();
        buildActionSettings();

        // Back button
        WButton backButton = add(theme.button("Back")).expandX().widget();
        backButton.action = () -> {
            if (this.parent instanceof HotkeysScreen s) {
                s.module.saveHotkeys();
                s.reload();
            }
            close();
        };
    }

    private void buildActionSettings() {
        actionSettingsTable.clear();

        switch (hotkey.action) {
            case SwitchSlot:
                actionSettingsTable.add(theme.label("Slot:"));
                WIntEdit slotWidget = theme.intEdit(hotkey.slot, 1, 9, true);
                slotWidget.action = () -> hotkey.slot = slotWidget.get();
                actionSettingsTable.add(slotWidget).expandX();
                actionSettingsTable.row();
                break;
            case HoldItem:
                // Item
                actionSettingsTable.add(theme.label("Item:"));
                WItem itemWidget = theme.item(new ItemStack(hotkey.item));
                WButton selectItemButton = theme.button("Select");
                selectItemButton.action = () -> {
                    ItemSetting tempSetting = new ItemSetting.Builder().name("item").defaultValue(hotkey.item).build();
                    ItemSettingScreen screen = new ItemSettingScreen(theme, tempSetting);
                    screen.onClosed(() -> {
                        hotkey.item = tempSetting.get();
                        itemWidget.set(new ItemStack(hotkey.item));
                    });
                    mc.setScreen(screen);
                };
                actionSettingsTable.add(itemWidget);
                actionSettingsTable.add(selectItemButton).expandX();
                actionSettingsTable.row();

                // NBT
                actionSettingsTable.add(theme.label("NBT:"));
                WTextBox nbtBox = theme.textBox(hotkey.nbt);
                nbtBox.action = () -> hotkey.nbt = nbtBox.get();
                actionSettingsTable.add(nbtBox).expandX();
                actionSettingsTable.row();
                break;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (WKeybind w : keybindWidgets) {
            if (w.onAction(false, button, 0)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int mods) {
        for (WKeybind w : keybindWidgets) {
            if (w.onAction(true, key, mods)) return true;
        }
        return super.keyPressed(key, scanCode, mods);
    }
} 