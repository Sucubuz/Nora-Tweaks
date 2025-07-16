package me.noramibu.tweaks.gui.screens;

import me.noramibu.tweaks.modules.HotkeyUtility;
import me.noramibu.tweaks.util.Hotkey;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HotkeysScreen extends WindowScreen {
    public final HotkeyUtility module;
    private WTable table;

    public HotkeysScreen(GuiTheme theme, HotkeyUtility module) {
        super(theme, "Configure Hotkeys");
        this.module = module;
    }

    @Override
    public void initWidgets() {
        table = add(theme.table()).expandX().widget();
        reload();
    }

    public void reload() {
        table.clear();
        for (Hotkey hotkey : module.hotkeys) {
            WLabel label = table.add(theme.label(hotkey.keybind.toString())).widget();

            WButton edit = theme.button("Edit");
            edit.action = () -> {
                EditHotkeyScreen screen = new EditHotkeyScreen(theme, module, hotkey);
                screen.parent = this;
                mc.setScreen(screen);
            };
            table.add(edit);

            WMinus remove = theme.minus();
            remove.action = () -> {
                module.hotkeys.remove(hotkey);
                module.saveHotkeys();
                reload();
            };
            table.add(remove);

            table.row();
        }

        WButton addButton = theme.button("Add");
        addButton.action = () -> {
            module.hotkeys.add(new Hotkey());
            module.saveHotkeys();
            reload();
        };
        table.add(addButton).expandX();
    }
} 