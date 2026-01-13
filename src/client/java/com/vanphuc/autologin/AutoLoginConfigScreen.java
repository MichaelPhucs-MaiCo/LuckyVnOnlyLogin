package com.vanphuc.autologin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoLoginConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget passwordField, firstSlotField, secondSlotField;

    public AutoLoginConfigScreen(Screen parent) {
        super(Text.of("Auto Login Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        int centerX = this.width / 2, centerY = this.height / 2;
        int fieldWidth = 200, fieldHeight = 20, spacing = 24, startY = centerY - 90;

        // Ô nhập mật khẩu
        this.passwordField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, fieldWidth, fieldHeight, Text.of(""));
        this.passwordField.setMaxLength(128); // Giới hạn độ dài để an toàn
        this.passwordField.setText(config.getPassword());
        this.addDrawableChild(this.passwordField);

        // Slot 1
        this.firstSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + spacing, fieldWidth, fieldHeight, Text.of(""));
        this.firstSlotField.setMaxLength(2);
        this.firstSlotField.setText(config.getFirstSlotString());
        this.addDrawableChild(this.firstSlotField);

        // Slot 2
        this.secondSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + 2 * spacing, fieldWidth, fieldHeight, Text.of(""));
        this.secondSlotField.setMaxLength(2);
        this.secondSlotField.setText(config.getSecondSlotString());
        this.addDrawableChild(this.secondSlotField);

        // Các nút bấm Toggle
        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Auto Click", config.isAutoClickEnabled()), b -> {
            config.setAutoClickEnabled(!config.isAutoClickEnabled());
            b.setMessage(getToggleButtonText("Auto Click", config.isAutoClickEnabled()));
        }).dimensions(centerX - 100, startY + 3 * spacing + 10, fieldWidth, fieldHeight).build());

        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Lịch sử HUD", config.isShowHistoryHud()), b -> {
            config.setShowHistoryHud(!config.isShowHistoryHud());
            b.setMessage(getToggleButtonText("Lịch sử HUD", config.isShowHistoryHud()));
        }).dimensions(centerX - 100, startY + 4 * spacing + 10, fieldWidth, fieldHeight).build());

        this.addDrawableChild(ButtonWidget.builder(getToggleButtonText("Thông báo nổi", config.isShowNotifications()), b -> {
            config.setShowNotifications(!config.isShowNotifications());
            b.setMessage(getToggleButtonText("Thông báo nổi", config.isShowNotifications()));
        }).dimensions(centerX - 100, startY + 5 * spacing + 10, fieldWidth, fieldHeight).build());

        // Nút Lưu & Thoát
        this.addDrawableChild(ButtonWidget.builder(Text.of("§aLưu & Thoát"), b -> {
            this.close(); // Gọi close() để thực hiện logic lưu và thoát
        }).dimensions(centerX - 100, startY + 6 * spacing + 20, fieldWidth, fieldHeight).build());
    }

    private Text getToggleButtonText(String name, boolean en) {
        return Text.of(name + ": " + (en ? "§aBật" : "§cTắt"));
    }

    /**
     * Hàm này được gọi khi đóng GUI (bấm nút Lưu hoặc bấm ESC)
     */
    @Override
    public void close() {
        // 1. Cập nhật dữ liệu từ các ô nhập vào Object Config
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        config.setPassword(this.passwordField.getText());
        config.setFirstSlotString(this.firstSlotField.getText());
        config.setSecondSlotString(this.secondSlotField.getText());

        // 2. Chỉ lưu file một lần duy nhất tại đây!
        AutoLoginConfig.save();

        // 3. Thoát về màn hình cha (Mod Menu)
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}