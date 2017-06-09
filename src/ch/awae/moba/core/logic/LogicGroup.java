package ch.awae.moba.core.logic;

import java.util.Arrays;

public final class LogicGroup {

    private final Logic members[];

    public LogicGroup(Logic[] members) {
        this.members = members;
    }

    public Logic[] toArray() {
        return Arrays.copyOf(this.members, this.members.length);
    }

    public Logic any() {
        return Logic.any(this.members);
    }

    public Logic all() {
        return Logic.all(this.members);
    }

    public Logic none() {
        return Logic.none(this.members);
    }

    public Logic count(int target) {
        return Logic.count(target, this.members);
    }

}
