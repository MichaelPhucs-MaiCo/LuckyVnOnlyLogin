package com.vanphuc.autologin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoLoginConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget passwordField, firstSlotField, secondSlotField;
    private ButtonWidget toggleAutoClickButton, toggleHistoryButton, toggleNotifButton;

    public AutoLoginConfigScreen(Screen parent) {
        super(Text.of("Auto Login Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        int centerX = this.width / 2, centerY = this.height / 2;
        int fieldWidth = 200, fieldHeight = 20, spacing = 24, startY = centerY - 90;

        // Các ô nhập liệu (Giữ nguyên)
        this.passwordField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, fieldWidth, fieldHeight, Text.of(""));
        this.passwordField.setText(config.getPassword());
        this.addDrawableChild(this.passwordField);

        this.firstSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + spacing, fieldWidth, fieldHeight, Text.of(""));
        this.firstSlotField.setText(config.getFirstSlotString());
        this.addDrawableChild(this.firstSlotField);

        this.secondSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + 2 * spacing, fieldWidth, fieldHeight, Text.of(""));
        this.secondSlotField.setText(config.getSecondSlotString());
        this.addDrawableChild(this.secondSlotField);

        // Nút Auto Click
        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Auto Click", config.isAutoClickEnabled()), b -> {
            config.setAutoClickEnabled(!config.isAutoClickEnabled());
            b.setMessage(getToggleButtonText("Auto Click", config.isAutoClickEnabled()));
        }).dimensions(centerX - 100, startY + 3 * spacing + 10, fieldWidth, fieldHeight).build());

        // Nút Lịch sử HUD
        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Lịch sử HUD", config.isShowHistoryHud()), b -> {
            config.setShowHistoryHud(!config.isShowHistoryHud());
            b.setMessage(getToggleButtonText("Lịch sử HUD", config.isShowHistoryHud()));
        }).dimensions(centerX - 100, startY + 4 * spacing + 10, fieldWidth, fieldHeight).build());

        // Nút Thông báo nổi (CÁI MỚI ĐÂY!) 🎯
        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Thông báo nổi", config.isShowNotifications()), b -> {
            config.setShowNotifications(!config.isShowNotifications());
            b.setMessage(getToggleButtonText("Thông báo nổi", config.isShowNotifications()));
        }).dimensions(centerX - 100, startY + 5 * spacing + 10, fieldWidth, fieldHeight).build());

        // Nút Lưu
        this.addDrawableChild(ButtonWidget.builder(Text.of("§aLưu & Thoát"), b -> this.client.setScreen(this.parent))
                .dimensions(centerX - 100, startY + 6 * spacing + 20, fieldWidth, fieldHeight).build());
    }

    private Text getToggleButtonText(String name, boolean en) {
        return Text.of(name + ": " + (en ? "§aBật" : "§cTắt"));
    }

    @Override public void close() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        config.setPassword(passwordField.getText());
        config.setFirstSlotString(firstSlotField.getText());
        config.setSecondSlotString(secondSlotField.getText());
        this.client.setScreen(this.parent);
    }
}