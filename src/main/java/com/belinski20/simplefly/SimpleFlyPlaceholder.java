package com.belinski20.simplefly;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class SimpleFlyPlaceholder extends PlaceholderExpansion {
    private final FlyManager fManager;

    public SimpleFlyPlaceholder(FlyManager fManager) {
        this.fManager = fManager;
    }

    public String onRequest(OfflinePlayer p, String identifier) {
        if (identifier.equals("enabled")) {
            if (p.isOnline() &&
                    this.fManager.flyContains(p.getPlayer()))
                return "ENABLED";
            return "DISABLED";
        }
        return null;
    }

    public boolean canRegister() {
        return true;
    }

    public boolean persist() {
        return true;
    }

    @NotNull
    public String getIdentifier() {
        return "SimpleFly";
    }

    @NotNull
    public String getAuthor() {
        return "Belinski20";
    }

    @NotNull
    public String getVersion() {
        return "1.0";
    }
}
