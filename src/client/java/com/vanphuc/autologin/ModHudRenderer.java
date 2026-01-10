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
    private static final Identifier HUD_ID = Identifier.of("autologin", "notification_hud");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static boolean showHistory = false; // Phím tắt Ctrl+Shift+Right điều khiển cái này

    public static void init() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            layeredDrawer.attachLayerAfter(IdentifiedLayer.SUBTITLES, HUD_ID, (drawContext, renderTickCounter) -> {
                render(drawContext);
            });
        });
    }

    public static void toggleHistory() { showHistory = !showHistory; }

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
        // THÊM CHECK: Nếu nhấn F1 (hudHidden) thì biến luôn cho sạch! 🧹
        if (client.player == null || client.options.hudHidden) return;

        AutoLoginConfig config = AutoLoginConfig.getInstance();
        TextRenderer renderer = client.textRenderer;
        int width = context.getScaledWindowWidth(), height = context.getScaledWindowHeight();
        long now = System.currentTimeMillis();

        activeNotifications.removeIf(n -> now > n.startTime + 5000);
        historyLog.removeIf(n -> now > n.startTime + 600000);

        // 1. VẼ LỊCH SỬ (Phải bật cả Phím tắt VÀ Setting) 📜
        if (showHistory && config.isShowHistoryHud()) {
            int hX = 10, hY = height / 4;
            context.drawText(renderer, Text.literal("§e§l--- LỊCH SỬ TU TIÊN ---"), hX, hY - 12, 0xFFFFFFFF, true);
            for (Notification n : historyLog) {
                String fullMsg = "§7" + n.timestamp + "§f" + n.text;
                context.fill(hX - 2, hY - 1, hX + renderer.getWidth(fullMsg) + 2, hY + 9, 0x60000000);
                context.drawText(renderer, Text.literal(fullMsg), hX, hY, 0xFFFFFFFF, true);
                hY += 10;
            }
        }

        // 2. VẼ THÔNG BÁO NỔI (Check Setting ở đây nè!) 🎯
        if (config.isShowNotifications() && !activeNotifications.isEmpty()) {
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
        String text, timestamp; long startTime;
        Notification(String t, String ts, long s) { text = t; timestamp = ts; startTime = s; }
    }
}