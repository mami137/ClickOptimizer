package com.subtick.mixin;

import com.subtick.hud.SubTickHud;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseInputMixin {

    @Inject(
        method = "onButton",
        at = @At("HEAD")
    )
    private void subtick$captureMouseButton(
            long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        
        if (action == 1) { // GLFW_PRESS
            if (info.button() == 0) {
                SubTickHud.recordLeftClick();
            } else if (info.button() == 1) {
                SubTickHud.recordRightClick();
            }
        }
    }
}
