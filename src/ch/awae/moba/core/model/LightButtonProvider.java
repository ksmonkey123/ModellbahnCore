package ch.awae.moba.core.model;

import ch.awae.utils.logic.Logic;

public class LightButtonProvider {

    private final static StrictButtonProvider provider = new StrictButtonProvider(Sector.LIGHT,
            "__buttons");

    public static Logic button(String id) {
        return provider.strict(id);
    }

}
