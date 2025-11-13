package de.zannagh.armorhider.mixin.client;

import com.ibm.icu.impl.UResource;
import de.zannagh.armorhider.config.ClientConfigManager;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptionsScreen.class)
public abstract class SkinOptionsMixin {

    @Shadow
    protected OptionListWidget body;

    @Inject(method = "init", at = @At("RETURN"))
    private void onAddOptions(CallbackInfo ci) {
        var current = ((GameOptionsScreen)(Object)this);
        if (!(current instanceof SkinOptionsScreen)) {
            return;
        }

        if (body == null) {
            return;
        }

        SimpleOption<Double> divider = new SimpleOption<>(
                "armorhider.helmet.divider",
                SimpleOption.emptyTooltip(),
                (text, value) -> Text.literal("Zannagh's Armor Hider"),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 0)
                        .withModifier(v -> v / 20.0, v -> (int) Math.round(v * 20)),
                ClientConfigManager.get().helmetTransparency, value -> { }
        );
        
        body.addSingleOptionEntry(divider);

        SimpleOption<Double> helmetOption = new SimpleOption<>(
                "armorhider.helmet.transparency",
                SimpleOption.constantTooltip(Text.literal("Adjusts the helmet transparency for your model between 0 and 1 in steps of 0.05.")),
                (text, value) -> Text.literal("Helmet: " + String.format("%.0f%%", value * 100)),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 20)
                        .withModifier(v -> v / 20.0, v -> (int) Math.round(v * 20)),
                ClientConfigManager.get().helmetTransparency,
                ClientConfigManager::setHelmetTransparency
        );
        
        body.addSingleOptionEntry(helmetOption);

        SimpleOption<Double> chestOption = new SimpleOption<>(
                "armorhider.chest.transparency",
                SimpleOption.constantTooltip(Text.literal("Adjusts the chestplate transparency for your model between 0 and 1 in steps of 0.05.")),
                (text, value) -> Text.literal("Chestplate: " + String.format("%.0f%%", value * 100)),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 20)
                        .withModifier(v -> v / 20.0, v -> (int) Math.round(v * 20)),
                ClientConfigManager.get().chestTransparency,
                ClientConfigManager::setChestTransparency
        );

        body.addSingleOptionEntry(chestOption);

        SimpleOption<Double> legsOption = new SimpleOption<>(
                "armorhider.legs.transparency",
                SimpleOption.constantTooltip(Text.literal("Adjusts the legs transparency for your model between 0 and 1 in steps of 0.05.")),
                (text, value) -> Text.literal("Leggings: " + String.format("%.0f%%", value * 100)),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 20)
                        .withModifier(v -> v / 20.0, v -> (int) Math.round(v * 20)),
                ClientConfigManager.get().legsTransparency,
                ClientConfigManager::setLegsTransparency
        );

        body.addSingleOptionEntry(legsOption);

        SimpleOption<Double> bootsOption = new SimpleOption<>(
                "armorhider.boots.transparency",
                SimpleOption.constantTooltip(Text.literal("Adjusts the boot transparency for your model between 0 and 1 in steps of 0.05.")),
                (text, value) -> Text.literal("Boots: " + String.format("%.0f%%", value * 100)),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 20)
                        .withModifier(v -> v / 20.0, v -> (int) Math.round(v * 20)),
                ClientConfigManager.get().bootsTransparency,
                ClientConfigManager::setBootsTransparency
        );

        body.addSingleOptionEntry(bootsOption);
    }
}
