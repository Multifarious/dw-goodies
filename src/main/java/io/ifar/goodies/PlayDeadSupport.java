package io.ifar.goodies;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nearly trivial implementation of
 */
public class PlayDeadSupport {

    private static final Logger LOG = LoggerFactory.getLogger(PlayDeadSupport.class);

    private static final AtomicBoolean dead = new AtomicBoolean(false);

    /**
     * Healthcheck implementation that wraps a {@code boolean} switch for marking the node as up or
     * down.
     *
     * @see #playDeadTask
     * @see #stopPlayingDeadTask
     */
    public static HealthCheck playDeadHealthCheck = new HealthCheck("playDead") {
        @Override
        protected Result check() throws Exception {
            if (dead.get()) {
                return Result.unhealthy("Marked as down.");
            } else {
                return Result.healthy("Marked as up.");
            }
        }
    };

    /**
     * Task to manually mark the node as "down".
     *
     * @see #playDeadHealthCheck
     * @see #stopPlayingDeadTask
     */
    public static Task playDeadTask = new Task("playDead") {
        @Override
        public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
            dead.set(true);
            LOG.info("Manually marked node as down.");
        }
    };

    /**
     * Task to manually mark the node as "up"; note that this will not override other healthchecks that might
     * cause the node to look down.
     *
     * @see #playDeadHealthCheck
     * @see #playDeadTask
     */
    public static Task stopPlayingDeadTask = new Task("stopPlayingDead") {
        @Override
        public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
            dead.set(false);
            LOG.info("Manually marked node as up.");
        }
    };
}
