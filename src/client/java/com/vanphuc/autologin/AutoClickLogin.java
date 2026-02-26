package com.vanphuc.autologin;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoClickLogin {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static boolean isRegistered = false;

    private long lastActionTime = 0;
    private long stateStartTime = 0;
    private long lastGlobalRetryTime = 0; // Bộ đếm vòng lặp 1 phút ⏰

    private int currentState = 0; // 0: Idle, 1: Đồng hồ, 2: Slot 1, 3: Slot 2, 4: Check Teleport
    private String lastGuiTitle = "";

    private final long ACTION_DELAY = 1500;
    private final long GLOBAL_RETRY_DELAY = 60000; // 1 phút (60,000 ms)
    private final long TIMEOUT_LIMIT = 15000;

    private int firstSlot;
    private int secondSlot;

    public AutoClickLogin() {
        if (!isRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
            isRegistered = true;
        }
    }

    private void loadConfig() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        try {
            this.firstSlot = Integer.parseInt(config.getFirstSlotString());
            this.secondSlot = Integer.parseInt(config.getSecondSlotString());
        } catch (Exception e) {
            this.firstSlot = 20; //Cụm SKYBLOCK
            this.secondSlot = 22; //SKY ENERGY
        }
    }

    public void enable() {
        if (currentState != 0) return;
        loadConfig();
        currentState = 1;
        stateStartTime = System.currentTimeMillis();
        lastGlobalRetryTime = System.currentTimeMillis(); // Bắt đầu tính 1 phút
        lastActionTime = System.currentTimeMillis();
        ChatUtils.addModMessage("🎯 Khởi động vòng lặp");
    }

    private void onTick(MinecraftClient client) {
        if (currentState == 0 || mc.player == null) return;

        long now = System.currentTimeMillis();

        // --- VÒNG LẶP 1 PHÚT CỦA MAI CỒ ---
        if (now - lastGlobalRetryTime >= GLOBAL_RETRY_DELAY) {
            if (mc.player.getInventory().getStack(4).getItem() == Items.CLOCK) {
                ChatUtils.debug("⏰ Đã qua 1 phút! Đóng GUI cũ và thử lại vòng lặp...");
                if (mc.currentScreen != null) mc.player.closeHandledScreen();

                currentState = 1;
                lastGlobalRetryTime = now;
                stateStartTime = now;
                lastActionTime = now;
                return;
            }
        }

        if (now - stateStartTime > TIMEOUT_LIMIT) {
            ChatUtils.addErrorMessage("⚠️ Kẹt State, đang thử bấm lại bước hiện tại...");
            stateStartTime = now;
        }

        switch (currentState) {
            case 1: // DÙNG ĐỒNG HỒ
                if (now - lastActionTime > ACTION_DELAY) {
                    if (mc.player.getInventory().getStack(4).getItem() == Items.CLOCK) {
                        mc.player.getInventory().setSelectedSlot(4);
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        ChatUtils.debug("👉 Use Đồng hồ.");
                        currentState = 2;
                        lastActionTime = now;
                        lastGuiTitle = "";
                    }
                }
                break;

            case 2: // CLICK CHỌN MÁY CHỦ (VD: SLOT 20)
                handleMenuClick(now, firstSlot, 3, "NETWORK");
                break;

            case 3: // CLICK VÀO CỤM (VD: SLOT 22)
                handleMenuClick(now, secondSlot, 4, "SKYBLOCK");
                break;

            case 4: // ĐỢI TELEPORT (CHECK TỌA ĐỘ)
                // Nếu X thoát khỏi vùng Lobby (-279)
                if (mc.player.getX() > -100) {
                    currentState = 0;
                    // PostLoginManager.start(); <-- Đã bay màu! 🕊️
                }
                break;
        }
    }

    private void handleMenuClick(long now, int slotId, int nextState, String expectedTitle) {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            String currentTitle = screen.getTitle().getString().toUpperCase();
            ScreenHandler handler = screen.getScreenHandler();

            if (currentTitle.contains(expectedTitle) || !currentTitle.equals(lastGuiTitle)) {
                if (slotId >= 0 && slotId < handler.slots.size()) {
                    Slot slot = handler.getSlot(slotId);
                    if (slot != null && slot.hasStack() && now - lastActionTime > ACTION_DELAY) {
                        mc.interactionManager.clickSlot(handler.syncId, slotId, 0, SlotActionType.PICKUP, mc.player);
                        ChatUtils.addModMessage("👉 Click " + expectedTitle + " (Slot " + slotId + ")");
                        lastActionTime = now;
                        lastGuiTitle = currentTitle;
                        currentState = nextState;
                    }
                }
            }
        }
    }
}