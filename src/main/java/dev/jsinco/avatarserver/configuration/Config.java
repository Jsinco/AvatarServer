package dev.jsinco.avatarserver.configuration;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;

public class Config {

    private static Config instance;
    private final SnakeYamlConfig yamlCfg;

    private Config() {
        yamlCfg = new SnakeYamlConfig("config.yml");
    }

    public String getHost() {
        return yamlCfg.getString("host");
    }

    public int getPort() {
        return yamlCfg.getInt("port");
    }

    public boolean useTextureHashes() {
        return yamlCfg.getBoolean("use-texture-hashes");
    }


    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
}
