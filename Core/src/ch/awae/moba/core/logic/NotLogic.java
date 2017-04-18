package ch.awae.moba.core.logic;

import ch.awae.moba.core.model.Model;

public class NotLogic implements Logic {

    private final Logic backer;

    public NotLogic(Logic backer) {
        this.backer = backer;
    }

    @Override
    public boolean evaluate(Model m) {
        return !this.backer.evaluate(m);
    }

    @Override
    public Logic not() {
        return this.backer;
    }

}
