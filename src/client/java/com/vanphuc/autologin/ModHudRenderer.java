package com.vanphuc.autologin;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ModHudRenderer {
    private static final List<Notification> activeNotifications = new ArrayList<>();
    private static final List<Notification> historyLog = new ArrayList<>();

    private static final int DISPLAY_TIME = 5000;
    private static final int HISTORY_EXPIRE = 600000; // 10 phút
    private static final Identifier HUD_ID = Identifier.of("autologin", "notification_hud");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static boolean showHistory = false;

    /**
     * Khởi tạo HUD - Đã fix lỗi GUI cho bản 1.21.4 🚀
     */
    public static void init() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            // Theo docs ông gửi: attachLayerAfter SUBTITLES sẽ Render after everything
            layeredDrawer.attachLayerAfter(IdentifiedLayer.SUBTITLES, HUD_ID, (drawContext, renderTickCounter) -> {
                render(drawContext);
            });
        });
    }

    public static void toggleHistory() {
        showHistory = !showHistory;
    }

    public static void addNotification(String text) {
        long now = System.currentTimeMillis();
        String timeStr = "[" + LocalTime.now().format(TIME_FORMAT) + "] ";

        Notification n = new Notification(text, timeStr, now);

        activeNotifications.add(n);
        historyLog.add(n);

        if (historyLog.size() > 25) historyLog.remove(0);
        if (activeNotifications.size() > 5) activeNotifications.remove(0);
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        TextRenderer renderer = client.textRenderer;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        long now = System.currentTimeMillis();

        activeNotifications.removeIf(n -> now > n.startTime + DISPLAY_TIME);
        historyLog.removeIf(n -> now > n.startTime + HISTORY_EXPIRE);

        // 1. VẼ LỊCH SỬ 📜
        if (showHistory) {
            int hX = 10;
            int hY = height / 4;
            context.drawText(renderer, Text.literal("§e§l--- LỊCH SỬ TU TIÊN (10 PHÚT) ---"), hX, hY - 12, 0xFFFFFFFF, true);

            for (Notification n : historyLog) {
                String fullMsg = "§7" + n.timestamp + "§f" + n.text;
                context.fill(hX - 2, hY - 1, hX + renderer.getWidth(fullMsg) + 2, hY + 9, 0x60000000);
                context.drawText(renderer, Text.literal(fullMsg), hX, hY, 0xFFFFFFFF, true);
                hY += 10;
            }
        }

        // 2. VẼ THÔNG BÁO 🎯
        if (!activeNotifications.isEmpty()) {
            int y = height - 100;
            for (int i = activeNotifications.size() - 1; i >= 0; i--) {
                String msg = activeNotifications.get(i).text;
                int textWidth = renderer.getWidth(msg);
                int x = (width - textWidth) / 2;

                context.fill(x - 4, y - 2, x + textWidth + 4, y + 10, 0x80000000);
                context.drawText(renderer, Text.literal(msg), x, y, 0xFFFFFFFF, true);
                y -= 12;
            }
        }
    }

    private static class Notification {
        String text;
        String timestamp;
        long startTime;

        Notification(String text, String timestamp, long startTime) {
            this.text = text;
            this.timestamp = timestamp;
            this.startTime = startTime;
        }
    }
}