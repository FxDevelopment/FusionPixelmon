package io.github.brendoncurmi.fusionpixelmon.sponge;

import io.github.brendoncurmi.fusionpixelmon.IPluginInfo;

public class PluginInfo implements IPluginInfo {
    public static final String ID = "fusionpixelmon";
    public static final String NAME = "FusionPixelmon";
    public static final String VERSION = "1.9";

    public static final String CMD_PERM = ID + ".command.";

    public static final String VERSIONS_ENDPOINT = "https://ore.spongepowered.org/api/v1/projects/" + ID + "/versions";
    public static final String ORE_VERSIONS = "https://ore.spongepowered.org/FusionDev/FusionPixelmon/versions";


    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getDownloadUrl() {
        return ORE_VERSIONS;
    }

    @Override
    public String getVersionsApiUrl() {
        return VERSIONS_ENDPOINT;
    }
}
