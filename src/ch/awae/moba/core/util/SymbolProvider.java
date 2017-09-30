package ch.awae.moba.core.util;

public final class SymbolProvider {

    // ============= SINGLETON STRUCTURE =============
    private static SymbolProvider INSTANCE = null;

    public static SymbolProvider getInstance() {
        if (INSTANCE == null)
            synchronized (SymbolProvider.class) {
                if (INSTANCE == null)
                    INSTANCE = new SymbolProvider();
            }
        return INSTANCE;
    }

    // ============== CLASS DEFINITION ===============
    private volatile long index = 0;

    private SymbolProvider() {
        super();
    }

    public synchronized String next() {
        return "$anon_#" + (this.index++);
    }

}
