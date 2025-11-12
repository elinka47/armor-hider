package de.zannagh.armorhider;

import net.minecraft.entity.EquipmentSlot;

public class ArmorTransparencyHelper {
    private static final ThreadLocal<EquipmentSlot> currentArmorSlot = ThreadLocal.withInitial(() -> null);

    public static void setCurrentArmorSlot(EquipmentSlot slot) {
        currentArmorSlot.set(slot);
    }

    public static EquipmentSlot getCurrentArmorSlot() {
        return currentArmorSlot.get();
    }

    public static void clearCurrentArmorSlot() {
        currentArmorSlot.remove();
    }
}
