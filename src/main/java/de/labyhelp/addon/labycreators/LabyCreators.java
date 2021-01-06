package de.labyhelp.addon.labycreators;

import de.labyhelp.addon.labycreators.listener.ClientJoinListener;
import de.labyhelp.addon.labycreators.listener.ClientMessageListener;
import de.labyhelp.addon.labycreators.utils.CreatorManager;
import de.labyhelp.addon.labycreators.utils.SettingsManager;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabyCreators extends LabyModAddon {

    private static LabyCreators instance;

    private final SettingsManager settingsManager = new SettingsManager();
    private final CreatorManager creatorsManager = new CreatorManager();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();


    @Override
    public void onEnable() {
        instance = this;

        try {
            String webVersion = readVersion();

            LabyCreators.getInstance().getSettingsManager().currentVersion = webVersion;
            if (!webVersion.equalsIgnoreCase(LabyCreators.getInstance().getSettingsManager().currentVersion)) {
                LabyCreators.getInstance().getSettingsManager().isNewerVersion = true;
            }
            LabyCreators.getInstance().getSettingsManager().serverResponding = true;
        } catch (Exception ignored) {
            LabyCreators.getInstance().getSettingsManager().serverResponding = false;
        }

        System.out.println("Loading Listeners");
        this.getApi().getEventManager().registerOnJoin(new ClientJoinListener());
        this.getApi().getEventManager().register(new ClientMessageListener());


    }

    @Override
    public void loadConfig() {
        LabyCreators.getInstance().getSettingsManager().addonEnabled = !this.getConfig().has("enable") || this.getConfig().get("enable").getAsBoolean();

    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        final BooleanElement enabled = new BooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), new Consumer<Boolean>() {
            @Override
            public void accept(final Boolean enable) {
                LabyCreators.getInstance().getSettingsManager().addonEnabled = enable;


                LabyCreators.this.getConfig().addProperty("enable", enable);
                LabyCreators.this.saveConfig();
            }
        }, LabyCreators.getInstance().getSettingsManager().getAddonEnabled());
        settings.add(enabled);

        settings.add(new HeaderElement(" "));
        settings.add(new HeaderElement("§lAddon Commands: /streamlist"));
        settings.add(new HeaderElement("§fFrom the LabyHelp Store // LabyHelp.de"));
        settings.add(new HeaderElement(" "));
    }

    public static LabyCreators getInstance() {
        return instance;
    }

    public void sendClientMessage(String message) {
        LabyMod.getInstance().displayMessageInChat(LabyCreators.getInstance().getSettingsManager().messagePrefix + message);
    }

    public void sendClientMessageDefault(String message) {
        LabyMod.getInstance().displayMessageInChat(message);
    }

    public ExecutorService getExecutor() {
        return threadPool;
    }
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
    public CreatorManager getCreatorsManager() {
        return creatorsManager;
    }

    public String readVersion() {
        try {
            final HttpURLConnection con = (HttpURLConnection) new URL("https://marvhuelsmann.de/labycreators.php").openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.connect();
            return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not read version!", e);
        }
    }
}
