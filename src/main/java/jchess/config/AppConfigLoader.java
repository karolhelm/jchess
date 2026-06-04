package jchess.config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
public class AppConfigLoader { //transforms json(text) into object AppConfig using Jackson library
    public static AppConfig load() throws Exception {//if not throwing  read value is red
        InputStream in = AppConfigLoader.class.getResourceAsStream("/config/app-config.json");
        return new ObjectMapper().readValue(in, AppConfig.class);
    }
}