package ch.awae.moba.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import ch.awae.utils.functional.T2;

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

    public List<T2<String, String>> getAll() {
        ArrayList<T2<String, String>> list = new ArrayList<>();
        for (Object _key : props.keySet()) {
            if (!(_key instanceof String))
                continue;
            String key = (String) _key;
            String value = props.getProperty(key);
            if (value == null)
                continue;
            list.add(T2.of(key, value));
        }
        return Collections.unmodifiableList(list);
    }

}
