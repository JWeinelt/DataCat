package de.julianweinelt.datacat.server.store;

import de.julianweinelt.datacat.server.store.yarn.Yarn;

import java.util.ArrayList;
import java.util.List;

public class YarnManager {
    private final List<Yarn> yarns = new ArrayList<>();

    public List<Yarn> getPopularYarns() {
        return yarns;
    }

}
