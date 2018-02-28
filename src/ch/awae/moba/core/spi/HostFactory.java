package ch.awae.moba.core.spi;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.Core;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.util.Utils;
import ch.awae.utils.functional.T2;

public final class HostFactory {

    private static final Logger logger = Utils.getLogger();

    private HostFactory() {
        throw new AssertionError("unreachable");
    }

    public static T2<SPIHost, Host> createHost(Sector sector, SPIChannel channel, String name,
            HostType type) {
        logger.info("host requirement: " + sector + " -- " + type);
        switch (type) {
            case BASIC: {
                final BlockingHost host = new BlockingHost(sector, channel, name);
                return new T2<>(host, host);
            }
            case LIGHTS: {
                final LightsHost host = new LightsHost(sector, channel, name);
                return new T2<>(host, host);
            }
            default:
                throw new AssertionError();
        }
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
                String tpe = props.getProperty(root + "type");
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
                HostType type = HostType.valueOf(tpe);
                // all is OK
                core.registerHost(channel, sector, title, type);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "failed loading host " + i, ex);
            }
        }
    }

}
