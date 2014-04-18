package io.ifar.goodies;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import javax.validation.Validation;

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
        return new Environment(name, Jackson.newObjectMapper(), Validation.buildDefaultValidatorFactory().getValidator(),
                               new MetricRegistry(),ClassLoader.getSystemClassLoader());
    }

}
