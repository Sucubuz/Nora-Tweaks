package me.noramibu.tweaks.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class Hotkey {
    public enum Action {
        SwitchSlot,
        HoldItem
    }

    public Action action;
    public Keybind keybind;
    public int presses;
    public int pressDelay;

    // SwitchSlot
    public int slot;

    // HoldItem
    public Item item;
    public String nbt;

    // transient state
    public transient int pressCount = 0;
    public transient int delayLeft = 0;

    public Hotkey() {
        this.action = Action.SwitchSlot;
        this.keybind = Keybind.none();
        this.presses = 1;
        this.pressDelay = 10;
        this.slot = 1;
        this.item = Items.AIR;
        this.nbt = "";
    }

    public void resetState() {
        pressCount = 0;
        delayLeft = 0;
    }

    public JsonObject toJson() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("action", action.name());
        object.add("keybind", gson.toJsonTree(keybind));
        object.addProperty("presses", presses);
        object.addProperty("pressDelay", pressDelay);
        object.addProperty("slot", slot);
        object.addProperty("item", Registries.ITEM.getId(item).toString());
        object.addProperty("nbt", nbt);
        return object;
    }

    public static Hotkey fromJson(JsonObject object) {
        Gson gson = new Gson();
        Hotkey hotkey = new Hotkey();

        if (object.has("action")) {
            try {
                hotkey.action = Action.valueOf(object.get("action").getAsString());
            } catch (IllegalArgumentException ignored) {}
        }
        if (object.has("keybind")) {
            hotkey.keybind = gson.fromJson(object.get("keybind"), Keybind.class);
        }
        if (object.has("presses")) {
            hotkey.presses = object.get("presses").getAsInt();
        }
        if (object.has("pressDelay")) {
            hotkey.pressDelay = object.get("pressDelay").getAsInt();
        }
        if (object.has("slot")) {
            hotkey.slot = object.get("slot").getAsInt();
        }
        if (object.has("item")) {
            Identifier id = Identifier.tryParse(object.get("item").getAsString());
            if (id != null) {
                hotkey.item = Registries.ITEM.get(id);
            }
        }
        if (object.has("nbt")) {
            hotkey.nbt = object.get("nbt").getAsString();
        }
        return hotkey;
    }
} 