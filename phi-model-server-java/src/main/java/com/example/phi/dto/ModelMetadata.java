package com.example.phi.dto;

public class ModelMetadata {
    private String name;
    private String version;
    private boolean ready;

    public ModelMetadata(String name, String version, boolean ready) {
        this.name = name;
        this.version = version;
        this.ready = ready;
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public boolean isReady() { return ready; }
}