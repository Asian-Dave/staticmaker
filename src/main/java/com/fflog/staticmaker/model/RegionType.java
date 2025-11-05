package com.fflog.staticmaker.model;

public enum RegionType {
    EUROPE("Europe", "eu"),
    AMERICA("America", "na"),
    JAPAN("Japan", "jp");

    private final String displayName;
    private final String fflogsCode;

    RegionType(String displayName, String fflogsCode) {
        this.displayName = displayName;
        this.fflogsCode = fflogsCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFflogsCode() {
        return fflogsCode;
    }

    public static RegionType fromDatacenter(String datacenter) {
        String dc = datacenter.toLowerCase();

        if (dc.equals("chaos") || dc.equals("light")) {
            return EUROPE;
        } else if (dc.equals("aether") || dc.equals("crystal") ||
                   dc.equals("dynamis") || dc.equals("primal")) {
            return AMERICA;
        } else if (dc.equals("elemental") || dc.equals("gaia") ||
                   dc.equals("mana") || dc.equals("meteor")) {
            return JAPAN;
        }

        throw new IllegalArgumentException("Unknown datacenter: " + datacenter);
    }
}
