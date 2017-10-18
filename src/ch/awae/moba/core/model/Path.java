package ch.awae.moba.core.model;

public final class Path {

    // ######## FIELDS ############
    public final String title;
    public final Sector sector;
    public final int    mask;
    public final int    data;
    public final int    priority;

    // ####### CONSTRUCTOR #######
    Path(String title, Sector sector, int mask, int data, int priority) {
        this.title = title;
        this.mask = mask;
        this.sector = sector;
        this.data = data;
        this.priority = priority;
    }

    // ######### METHODS #########
    public boolean collides(Path other) {
        return (this.sector == other.sector) && ((this.mask & other.mask) != 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + data;
        result = prime * result + mask;
        result = prime * result + priority;
        result = prime * result + ((sector == null) ? 0 : sector.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Path))
            return false;
        Path other = (Path) obj;
        if (data != other.data)
            return false;
        if (mask != other.mask)
            return false;
        if (priority != other.priority)
            return false;
        if (sector != other.sector)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }
    
    // background command issuing
    

}
