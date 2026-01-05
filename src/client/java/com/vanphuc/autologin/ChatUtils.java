package com.vanphuc.autologin;

import net.minecraft.client.MinecraftClient;

/**
 * ChatUtils – Trạm trung chuyển thông tin "tuyệt mật" của con bot.
 * Đã nâng cấp: Hỗ trợ Debug và History Log xịn xò. 🚀
 */
public class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Prefix với icon cho nó sinh động nhé Mai Cồ! 😎
    private static final String PREFIX = "§b[AutoLogin] §f";
    private static final String DEBUG_PREFIX = "§a[Debug ⚙️] §7";
    private static final String ERROR_PREFIX = "§c[Lỗi ❌] §f";

    public static void tick() {
        // Có thể thêm logic đếm tick ở đây nếu cần chống spam log
    }

    /**
     * Gửi tin nhắn hoặc lệnh ra Server.
     */
    public static void sendPlayerMsg(String message) {
        if (mc.player == null || mc.player.networkHandler == null || message == null) return;

        if (message.startsWith("#")) {
            // Gửi lệnh cho Baritone
            mc.player.networkHandler.sendChatMessage(message);
        } else if (message.startsWith("/")) {
            // Gửi lệnh Minecraft chính thống (không gạch chéo đầu)
            mc.player.networkHandler.sendChatCommand(message.substring(1));
        } else {
            // Chat bình thường
            mc.player.networkHandler.sendChatMessage(message);
        }
    }

    /**
     * Thông báo thông thường - Hiện lên HUD và lưu History.
     */
    public static void addModMessage(String message) {
        ModHudRenderer.addNotification(PREFIX + message);
    }

    /**
     * Thông báo lỗi - Hiện màu đỏ rực cảnh báo.
     */
    public static void addErrorMessage(String message) {
        ModHudRenderer.addNotification(ERROR_PREFIX + message);
    }

    /**
     * Thông báo Debug - Dành cho lúc soi xem bot đang kẹt ở State nào. 🕵️‍♂️
     */
    public static void debug(String message) {
        ModHudRenderer.addNotification(DEBUG_PREFIX + message);
    }
}