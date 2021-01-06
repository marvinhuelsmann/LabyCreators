package de.labyhelp.addon.labycreators.listener;

import de.labyhelp.addon.labycreators.LabyCreators;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.main.LabyMod;
import net.labymod.utils.UUIDFetcher;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;
import java.util.UUID;

public class ClientMessageListener implements MessageSendEvent {

    int i = 0;

    @Override
    public boolean onSend(String s) {

        if (LabyCreators.getInstance().getSettingsManager().addonEnabled) {
            if (s.startsWith("/addstream")) {
                LabyCreators.getInstance().getExecutor().submit(() -> {
                    LabyCreators.getInstance().getCreatorsManager().readCreators();
                    if (LabyCreators.getInstance().getCreatorsManager().getCreators().contains(LabyMod.getInstance().getPlayerUUID().toString())) {
                        if (LabyCreators.getInstance().getSettingsManager().sendAnnounce != 1) {
                            LabyCreators.getInstance().getCreatorsManager().sendLive(LabyMod.getInstance().getPlayerUUID());
                            LabyCreators.getInstance().sendClientMessage(EnumChatFormatting.GREEN + "Your stream is now entered for 2 hours");

                            LabyCreators.getInstance().getSettingsManager().sendAnnounce = 1;
                        } else {
                            LabyCreators.getInstance().sendClientMessage("Please wait or asked an Moderator or Administrator..");
                        }
                    } else {
                        LabyCreators.getInstance().sendClientMessage(EnumChatFormatting.RED + "You dont have permissions!");
                    }
                });
                return true;
            } else if (s.startsWith("/streamlist")) {
                LabyCreators.getInstance().getExecutor().submit(() -> {
                    LabyCreators.getInstance().getCreatorsManager().readLive();
                    LabyCreators.getInstance().sendClientMessage("Active Livestreams:");

                    for (Map.Entry<String, String> streams : LabyCreators.getInstance().getCreatorsManager().getLiveStreams().entrySet()) {
                        LabyCreators.getInstance().sendClientMessage(UUIDFetcher.getName(UUID.fromString(streams.getKey())));
                        LabyCreators.getInstance().sendClientMessage("https://" + streams.getValue());
                        LabyCreators.getInstance().sendClientMessageDefault("");
                    }
                });

                return true;
            }
        } else {
            i++;

            if (i == 1) {
                LabyCreators.getInstance().sendClientMessage("You have deactivated the Addon!");
            }
            return false;
        }

        return false;
    }
}
