package io.ifar.goodies;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.util.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for {@link io.ifar.goodies.CachedHealthCheckDecorator}.
 */
@RunWith(JUnit4.class)
public class CachedHealthCheckDecoratorTest {

    private FailOnSecondCallHealthCheck failOnSecondCall;

    private class FailOnSecondCallHealthCheck extends HealthCheck {

        final AtomicInteger tally = new AtomicInteger(0);

        @Override
        public Result check() throws Exception {
            if (tally.getAndIncrement() > 0) {
                return HealthCheck.Result.unhealthy("Failed.");
            } else {
                return HealthCheck.Result.healthy();
            }
        }
    }

    @Before
    public void setup() {
        failOnSecondCall = new FailOnSecondCallHealthCheck();
    }

    @Test
    public void validateFailsOnSecondCall() throws Exception {
        Assert.assertTrue(failOnSecondCall.check().isHealthy());
        Assert.assertFalse(failOnSecondCall.check().isHealthy());
    }

    @Test
    public void healthcheckResultIsCached() throws Exception {
        CachedHealthCheckDecorator wrapped = new CachedHealthCheckDecorator(failOnSecondCall, Duration.seconds(3L));

        Assert.assertTrue(wrapped.check().isHealthy());
        Assert.assertTrue(wrapped.check().isHealthy());

        Thread.sleep(3000L);
        Assert.assertFalse(wrapped.check().isHealthy());
    }

}
