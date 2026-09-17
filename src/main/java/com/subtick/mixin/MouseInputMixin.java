package com.subtick.mixin;

import com.subtick.hud.SubTickHud;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseInputMixin {

    @Inject(
        method = "onMouseButton",
        at = @At("HEAD")
    )
    private void subtick$captureMouseButton(
            long window, int button, int action, int modifiers, CallbackInfo ci) {
        
        if (action == 1) { // GLFW_PRESS
            if (button == 0) {
                SubTickHud.recordLeftClick();
            } else if (button == 1) {
                SubTickHud.recordRightClick();
            }
        }
    }
}
