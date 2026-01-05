    package com.vanphuc.autologin;

    import com.terraformersmc.modmenu.api.ConfigScreenFactory;
    import com.terraformersmc.modmenu.api.ModMenuApi;

    // Class này implement ModMenuApi để liên kết với Mod Menu
    public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            // Trả về một thể hiện của màn hình cài đặt của bạn
            return AutoLoginConfigScreen::new;
        }
    }