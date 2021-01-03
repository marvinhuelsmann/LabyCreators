package de.marvhuelsmann.labycreators.utils;

import net.minecraft.util.EnumChatFormatting;

public class SettingsManager {

    public boolean addonEnabled = true;
    public final String messagePrefix = EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.DARK_PURPLE + "LabyCreators" + EnumChatFormatting.DARK_GRAY + "] " + EnumChatFormatting.GRAY;
    public boolean serverResponding = false;
    public Boolean joinMessage = true;

    public boolean getAddonEnabled() {
        return addonEnabled;
    }

    public String currentVersion = "1.0";
    public Boolean isNewerVersion = false;
    public boolean isNewerVersion() {
        return isNewerVersion;
    }

    public int sendAnnounce = 0;

}
