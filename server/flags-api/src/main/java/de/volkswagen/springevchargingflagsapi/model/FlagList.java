package de.volkswagen.springevchargingflagsapi.model;

import java.util.ArrayList;
import java.util.List;

public class FlagList {
    private final List<Flag> flagList;

    public FlagList() {
        this.flagList = new ArrayList<>();
    }

    public FlagList(List<Flag> flagList) {
        this.flagList = flagList;
    }

    public List<Flag> getFlags() {
        return this.flagList;
    }
}
