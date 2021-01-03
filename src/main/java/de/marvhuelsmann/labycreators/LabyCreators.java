package de.marvhuelsmann.labycreators;

import de.marvhuelsmann.labycreators.listener.ClientJoinListener;
import de.marvhuelsmann.labycreators.listener.ClientMessageListener;
import de.marvhuelsmann.labycreators.utils.CreatorManager;
import de.marvhuelsmann.labycreators.utils.SettingsManager;
import de.marvhuelsmann.labycreators.utils.Updater;
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

    private static LabyCreators instace;

    private final Updater updater = new Updater();
    private final SettingsManager settingsManager = new SettingsManager();
    private final CreatorManager creatorsManager = new CreatorManager();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();


    @Override
    public void onEnable() {
        instace = this;
        try {
            String webVersion = readVersion();

            LabyCreators.getInstace().getSettingsManager().currentVersion = webVersion;
            if (!webVersion.equalsIgnoreCase(LabyCreators.getInstace().getSettingsManager().currentVersion)) {
                LabyCreators.getInstace().getSettingsManager().isNewerVersion = true;
            }
            LabyCreators.getInstace().getSettingsManager().serverResponding = true;
        } catch (Exception ignored) {
            LabyCreators.getInstace().getSettingsManager().serverResponding = false;
        }

        System.out.println("Loading Listeners");
        this.getApi().getEventManager().registerOnJoin(new ClientJoinListener());
        this.getApi().getEventManager().register(new ClientMessageListener());


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (LabyCreators.getInstace().getSettingsManager().isNewerVersion()) {
                    LabyCreators.getInstace().getUpdater().update();
                }
            }
        }));
    }

    @Override
    public void loadConfig() {
        LabyCreators.getInstace().getSettingsManager().addonEnabled = !this.getConfig().has("enable") || this.getConfig().get("enable").getAsBoolean();

    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        final BooleanElement enabled = new BooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), new Consumer<Boolean>() {
            @Override
            public void accept(final Boolean enable) {
                LabyCreators.getInstace().getSettingsManager().addonEnabled = enable;


                LabyCreators.this.getConfig().addProperty("enable", enable);
                LabyCreators.this.saveConfig();
            }
        }, LabyCreators.getInstace().getSettingsManager().getAddonEnabled());
        settings.add(enabled);

        settings.add((SettingsElement) new HeaderElement(" "));
        settings.add((SettingsElement) new HeaderElement("§lAddon Commands: /streamlist"));
        settings.add((SettingsElement) new HeaderElement("§fFrom the LabyHelp Store // LabyHelp.de"));
        settings.add((SettingsElement) new HeaderElement(" "));
    }

    public static LabyCreators getInstace() {
        return instace;
    }

    public void sendClientMessage(String message) {
        LabyMod.getInstance().displayMessageInChat(LabyCreators.getInstace().getSettingsManager().messagePrefix + message);
    }

    public void sendClientMessageDefault(String message) {
        LabyMod.getInstance().displayMessageInChat(message);
    }

    public ExecutorService getExecutor() {
        return threadPool;
    }
    public Updater getUpdater() {
        return updater;
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
