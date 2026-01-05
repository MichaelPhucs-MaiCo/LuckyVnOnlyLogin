package com.vanphuc.autologin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoLoginLuckyVNClient implements ClientModInitializer {
    public static final String MOD_ID = "auto-login-luckyvn";
    private AutoClickLogin autoClickLogin;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean isLoginPromptReceived = false;
    private long lastLoginAttemptTime = 0;
    private final long loginDelayMillis = 5_000;

    private final BlockPos LOGIN_POS = new BlockPos(-279, 39, 468);
    private final double TOLERANCE = 1.5;

    // PHÍM TẮT XEM LỊCH SỬ LOG ⌨️
    private static KeyBinding historyKey;

    @Override
    public void onInitializeClient() {
        this.autoClickLogin = new AutoClickLogin();

        // Khởi tạo bộ vẽ HUD
        ModHudRenderer.init();

        // ĐĂNG KÝ PHÍM TẮT: Mặc định là phím Mũi tên phải
        historyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Xem lịch sử Log (Ctrl+Shift+C)",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT,
                "Auto Login LuckyVN"
        ));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            resetState();
            // Đã gỡ bỏ PostLoginManager.stop()
            ChatUtils.addModMessage("Trạng thái bot đã được reset.");
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ChatUtils.tick();
            // Đã gỡ bỏ PostLoginManager.tick()

            if (historyKey.wasPressed()) {
                if (Screen.hasControlDown() && Screen.hasShiftDown()) {
                    ModHudRenderer.toggleHistory();
                }
            }

            if (mc.player == null || mc.world == null) return;

            long now = System.currentTimeMillis();

            if (isInLoginArea() && !isLoginPromptReceived && now - lastLoginAttemptTime >= loginDelayMillis) {
                sendLoginCommand();
                lastLoginAttemptTime = now;
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String messageText = message.getString();

            // Đã gỡ bỏ PostLoginManager.onChatMessage(messageText)

            if (messageText.contains("/login") && messageText.contains("mật khẩu")) {
                if (!isLoginPromptReceived) {
                    sendLoginCommand();
                    isLoginPromptReceived = true;
                }
            }

            if (messageText.contains("Đăng nhập thành công! Nhớ điểm danh hằng ngày tại")) {
                if (AutoLoginConfig.getInstance().isAutoClickEnabled()) {
                    autoClickLogin.enable();
                    ChatUtils.addModMessage("Đã đăng nhập thành công. Chờ AutoClick...");
                }
                isLoginPromptReceived = false;
            }
        });
    }

    public void resetState() {
        isLoginPromptReceived = false;
        lastLoginAttemptTime = 0;
    }

    private void sendLoginCommand() {
        String password = AutoLoginConfig.getInstance().getPassword();
        if (password != null && !password.isEmpty()) {
            ChatUtils.sendPlayerMsg("/login " + password);
        }
    }

    private boolean isInLoginArea() {
        if (mc.player == null) return false;
        Vec3d pos = mc.player.getPos();
        return pos.distanceTo(LOGIN_POS.toCenterPos()) < TOLERANCE;
    }
}