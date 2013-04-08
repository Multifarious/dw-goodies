package io.ifar.goodies;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.yammer.dropwizard.validation.Validator;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CliConveniences {

    public static void quietLogging(String... packages) {
        for (String pkg : packages) {
            ((Logger) LoggerFactory.getLogger(pkg)).setLevel(Level.OFF);
        }
    }

    public static Environment fabricateEnvironment(String name, Configuration configuration) {
        return new Environment(name, configuration, new ObjectMapperFactory(), new Validator());
    }

}
