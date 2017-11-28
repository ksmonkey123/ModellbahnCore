package ch.awae.moba.core.operators;

import java.util.logging.Logger;

import ch.awae.moba.core.util.Utils;

class PureOperator implements IOperator {

    private volatile boolean enabled = false;

    private final Logger logger = Utils.getLogger();

    private final IOperation instance;
    private final String     name;

    public PureOperator(String name, IOperation backer) {
        this.name = name;
        this.instance = backer;
    }

    @Override
    public void update() {
        if (!this.enabled) {
            this.logger.warning("operator '" + this.name + "' is disabled!");
            return;
        }
        this.instance.update();
    }

    @Override
    public synchronized void start() {
        instance.onStart();
        this.enabled = true;
    }

    @Override
    public synchronized void halt() {
        this.enabled = false;
        instance.onStop();
    }

    @Override
    public boolean isActive() {
        return this.enabled;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
