package no.cantara.jau.util;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 17.06.15.
 */
public class AppConfig {
    private static final Logger log = getLogger(AppConfig.class);
    final ConstrettoConfiguration configuration;

    private static volatile AppConfig instance;


    private AppConfig(String appname) {
        if (appname == null) {
            throw new IllegalArgumentException("appname is null. Please run AppName.init(\"myawesomeapp\") before reading configuration");
        }
        String appnameDotProperties = "classpath:" + appname + ".properties";
        String appnameOverridesDotProperties = "file:" + System.getProperty("user.dir")+
                File.separator + "config_override"+
                File.separator + appname +
                "_overrides.properties";
        System.out.println("Properties Loaded from: Commandline - "+ appnameOverridesDotProperties+ " - "+appnameDotProperties );
        configuration = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(Resource.create(appnameDotProperties))
                .addResource(Resource.create(appnameOverridesDotProperties))
                .done()
                .getConfiguration();
        printConfiguration(configuration);
    }

    public static String getString(String key) {
        try {
            return getInstance().configuration.evaluateToString(key);
        } catch (Exception e) {
            return null;
        }
    }
    public static void init(String appname) {
        instance = new AppConfig(appname);
        getInstance();
    }

    public static AppConfig getInstance() {
        return instance;
    }

    private static void printConfiguration(ConstrettoConfiguration configuration) {
        Map<String, String> properties = configuration.asMap();
        for (String key : properties.keySet()) {
            log.info("Loading Property: {}, value: {}", key, properties.get(key));
        }
    }
}