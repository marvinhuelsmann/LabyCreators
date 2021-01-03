package de.marvhuelsmann.labycreators.listener;

import de.marvhuelsmann.labycreators.LabyCreators;
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

        if (LabyCreators.getInstace().getSettingsManager().addonEnabled) {
            if (s.startsWith("/addstream")) {
                LabyCreators.getInstace().getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        LabyCreators.getInstace().getCreatorsManager().readCreators();
                        if (LabyCreators.getInstace().getCreatorsManager().getCreators().contains(LabyMod.getInstance().getPlayerUUID().toString())) {
                            if (LabyCreators.getInstace().getSettingsManager().sendAnnounce != 1) {
                                LabyCreators.getInstace().getCreatorsManager().sendLive(LabyMod.getInstance().getPlayerUUID());
                                LabyCreators.getInstace().sendClientMessage(EnumChatFormatting.GREEN + "Your stream is now entered for 2 hours");

                                LabyCreators.getInstace().getSettingsManager().sendAnnounce = 1;
                            } else {
                                LabyCreators.getInstace().sendClientMessage("Please wait or asked an Moderator or Administrator..");
                            }
                        } else {
                            LabyCreators.getInstace().sendClientMessage(EnumChatFormatting.RED + "You dont have permissions!");
                        }
                    }
                });
                return true;
            } else if (s.startsWith("/streamlist")) {
                LabyCreators.getInstace().getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        LabyCreators.getInstace().getCreatorsManager().readLive();
                        LabyCreators.getInstace().sendClientMessage("Active Livestreams:");

                        for (Map.Entry<String, String> streams : LabyCreators.getInstace().getCreatorsManager().getLiveStreams().entrySet()) {
                            LabyCreators.getInstace().sendClientMessage(UUIDFetcher.getName(UUID.fromString(streams.getKey())));
                            LabyCreators.getInstace().sendClientMessage("https://" + streams.getValue());
                            LabyCreators.getInstace().sendClientMessageDefault("");
                        }
                    }
                });

                return true;
            }
        } else {
            i++;

            if (i == 1) {
                LabyCreators.getInstace().sendClientMessage("You have deactivated the Addon!");
            }
            return false;
        }

        return false;
    }
}
