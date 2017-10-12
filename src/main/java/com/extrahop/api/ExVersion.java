package com.extrahop.api;

public class ExVersion {
    private String branch;
    private String version;

    public ExVersion(String branch, String version) {
        this.branch = branch;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getBranch() {
        return branch;
    }
}