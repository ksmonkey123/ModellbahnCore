package ch.awae.moba.core.model;

import ch.awae.utils.logic.Logic;
import ch.awae.utils.logic.LogicGroup;

public class StrictButtonProvider extends ButtonProvider {

    private final LogicGroup domain;

    public StrictButtonProvider(Sector sector, String domain) {
        super(sector);
        this.domain = group(domain);
    }

    public Logic strict(String id) {
        return group(id).strict(domain);
    }

}
