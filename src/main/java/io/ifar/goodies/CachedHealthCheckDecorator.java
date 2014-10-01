package io.ifar.goodies;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.dropwizard.util.Duration;

import javax.annotation.Nonnull;

/**
 * Wrapper for a {@link com.codahale.metrics.health.HealthCheck} that caches the result for a period of time before
 * re-invoking the underlying healthcheck.  Introduces latency but shields underlying systems from time-consuming or
 * resource-intensive checks.
 */
public class CachedHealthCheckDecorator extends HealthCheck {

    public static final String MAGIC = "magic";
    private final LoadingCache<String, Result> cache;

    public CachedHealthCheckDecorator(final HealthCheck inner, @Nonnull Duration ttl) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(ttl.getQuantity(), ttl.getUnit())
                .maximumSize(1)
                .build(new CacheLoader<String, HealthCheck.Result>() {
                    @Override
                    public HealthCheck.Result load(@Nonnull String key) throws Exception {
                        return inner.execute();
                    }
                });
    }

    @Override
    protected Result check() throws Exception {
        return cache.get(MAGIC);
    }
}
