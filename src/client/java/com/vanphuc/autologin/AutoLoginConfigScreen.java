package com.vanphuc.autologin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoLoginConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget passwordField;
    private TextFieldWidget firstSlotField;
    private TextFieldWidget secondSlotField;
    private ButtonWidget toggleAutoClickButton;

    public AutoLoginConfigScreen(Screen parent) {
        super(Text.of("Auto Login LuckyVN Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int spacing = 25;
        // Chỉnh lại startY để các ô nằm chính giữa màn hình hơn
        int startY = centerY - 60;

        // Ô nhập mật khẩu
        this.passwordField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, fieldWidth, fieldHeight, Text.of(""));
        this.passwordField.setText(config.getPassword());
        this.passwordField.setPlaceholder(Text.of("Mật khẩu"));
        this.addDrawableChild(this.passwordField);

        // Ô nhập Slot thứ nhất
        this.firstSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + spacing, fieldWidth, fieldHeight, Text.of(""));
        this.firstSlotField.setText(config.getFirstSlotString());
        this.firstSlotField.setPlaceholder(Text.of("Slot thứ nhất (ví dụ: 22)"));
        this.addDrawableChild(this.firstSlotField);

        // Ô nhập Slot thứ hai
        this.secondSlotField = new TextFieldWidget(this.textRenderer, centerX - 100, startY + 2 * spacing, fieldWidth, fieldHeight, Text.of(""));
        this.secondSlotField.setText(config.getSecondSlotString());
        this.secondSlotField.setPlaceholder(Text.of("Slot thứ hai (ví dụ: 30)"));
        this.addDrawableChild(this.secondSlotField);

        // Nút Bật/Tắt Auto Click
        this.toggleAutoClickButton = ButtonWidget.builder(getToggleButtonText("Click", config.isAutoClickEnabled()), button -> {
            boolean isEnabled = !config.isAutoClickEnabled();
            config.setAutoClickEnabled(isEnabled);
            button.setMessage(getToggleButtonText("Click", isEnabled));
        }).dimensions(centerX - 100, startY + 3 * spacing + 10, fieldWidth, fieldHeight).build();
        this.addDrawableChild(this.toggleAutoClickButton);

        // Nút Lưu & Thoát
        this.addDrawableChild(ButtonWidget.builder(Text.of("Lưu & Thoát"), button -> {
            saveAndClose();
        }).dimensions(centerX - 100, startY + 4 * spacing + 15, fieldWidth, fieldHeight).build());
    }

    private void saveAndClose() {
        AutoLoginConfig config = AutoLoginConfig.getInstance();
        config.setPassword(this.passwordField.getText());
        config.setFirstSlotString(this.firstSlotField.getText());
        config.setSecondSlotString(this.secondSlotField.getText());

        this.client.setScreen(this.parent);
    }

    @Override
    public void close() {
        this.saveAndClose();
    }

    private Text getToggleButtonText(String functionName, boolean isEnabled) {
        return isEnabled ? Text.of("Tự động " + functionName + " Đã Bật") : Text.of("Tự động " + functionName + " Đã Tắt");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (this.passwordField.mouseClicked(mouseX, mouseY, button)
                || this.firstSlotField.mouseClicked(mouseX, mouseY, button)
                || this.secondSlotField.mouseClicked(mouseX, mouseY, button)) {
            handled = true;
        }
        return handled;
    }
}