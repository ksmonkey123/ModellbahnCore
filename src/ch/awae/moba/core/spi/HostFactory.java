package ch.awae.moba.core.spi;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.Core;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.util.Pair;
import ch.awae.moba.core.util.Utils;

public final class HostFactory {

    private static final Logger logger = Utils.getLogger();

    private HostFactory() {
        throw new AssertionError("unreachable");
    }

    public static Pair<SPIHost, Host> createHost(SPIChannel channel, String name) {
        final HostImpl host = new HostImpl(channel, name);
        return new Pair<>(host, host);
    }

    public static void loadHosts(Core core) throws IOException {
        logger.info("Loading Host configuration");
        Properties props = Configs.getProperties("hosts");
        for (int i = 0; i < SPIChannel.getChannelCount(); i++) {
            try {
                String root = "host_" + i + ".";
                boolean enabled = Boolean
                        .parseBoolean(props.getProperty(root + "enabled", "false"));
                String sect = props.getProperty(root + "sector");
                String title = props.getProperty(root + "title");
                // check if all is OK
                if (sect == null || title == null) {
                    logger.info("Skipping host " + i + " due to missing entries");
                    continue;
                }
                if (!enabled) {
                    logger.info("Skipping disabled host " + i);
                    continue;
                }
                SPIChannel channel = SPIChannel.getChannelByIndex(i);
                Sector sector = Sector.valueOf(sect);
                // all is OK
                core.registerHost(channel, sector, title);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "failed loading host " + i, ex);
            }
        }
    }

}
