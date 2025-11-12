package de.zannagh.armorhider.mixin.client;

import de.zannagh.armorhider.ArmorTransparencyHelper;
import de.zannagh.armorhider.ClientConfigManager;
import de.zannagh.armorhider.PlayerConfig;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelPart.class)
public class ModelPartMixin {

    @ModifyVariable(
            method = "renderCuboids(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;III)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 2
    )
    private int modifyAlpha(int alpha) {
        EquipmentSlot slot = ArmorTransparencyHelper.getCurrentArmorSlot();
        // Only modify if we're currently rendering armor
        if (slot == null) {
            return alpha;
        }

        PlayerConfig config = ClientConfigManager.get();
        double transparency = switch (slot) {
            case HEAD -> config.helmetTransparency;
            case CHEST -> config.chestTransparency;
            case LEGS -> config.legsTransparency;
            case FEET -> config.bootsTransparency;
            default -> 1.0;
        };

        // Only apply transparency if it's less than fully opaque
        // This way we don't affect other model parts
        if (transparency >= 0.99) {
            return alpha;
        }

        // Modify alpha value based on transparency
        return (int) (alpha * transparency);
    }
}
