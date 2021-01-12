package de.labyhelp.addon.labycreators.listener;

import de.labyhelp.addon.LabyHelp;
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

                            LabyCreators.getInstance().getCreatorsManager().sendLive();
                            LabyHelp.getInstance().sendTranslMessage("labycreators.enterstream");

                            LabyCreators.getInstance().getSettingsManager().sendAnnounce = 1;
                        } else {
                            LabyHelp.getInstance().sendTranslMessage("labycreators.ask");
                        }
                    } else {
                        LabyHelp.getInstance().sendTranslMessage("main.noperms");
                    }
                });
                return true;
            } else if (s.startsWith("/streamlist")) {
                LabyCreators.getInstance().getExecutor().submit(() -> {
                    LabyCreators.getInstance().getCreatorsManager().getLiveStreams().clear();
                    LabyCreators.getInstance().getCreatorsManager().readLive();
                    LabyHelp.getInstance().sendTranslMessage("labycreators.activestream");

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
                LabyHelp.getInstance().sendTranslMessage("main.dis");
            }
            return false;
        }

        return false;
    }
}
