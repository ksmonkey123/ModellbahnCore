package ch.awae.moba.core.util;

import java.util.Objects;
import java.util.Properties;

public final class Props {

    private final Properties props;

    public Props(Properties props) {
        this.props = Objects.requireNonNull(props, "props may not be 'null'");
    }

    // ==== ACCESSORS ====
    public String get(String key) {
        return this.props.getProperty(key);
    }

    public int getInt(String key) {
        return Utils.parseInt(this.props.getProperty(key));
    }

}
