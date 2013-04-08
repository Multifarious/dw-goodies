package io.ifar.goodies;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Pull the EC2 instance ID into healthcheck property.
 */
public class Ec2InstanceIdHealthCheck extends HealthCheck {

    private static final Logger LOG = LoggerFactory.getLogger(Ec2InstanceIdHealthCheck.class);
    public static final String EC2_LOCAL_METADATA_URL = "http://169.254.169.254/latest/meta-data/instance-id";

    private String instanceId;

    public Ec2InstanceIdHealthCheck() {
        super(Ec2InstanceIdHealthCheck.class.getSimpleName());
        URL u;
        try {
            u = new URL(EC2_LOCAL_METADATA_URL);
        } catch (MalformedURLException mue) {
            LOG.error("EC2 local metadata URL {} was flagged as malformed: ({}) {}",
                    EC2_LOCAL_METADATA_URL, mue.getClass().getSimpleName(), mue.getMessage());
            return;
        }
        final URLConnection urlConnection;
        try {
            urlConnection = u.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
            instanceId = Joiner.on("\n").join(CharStreams.readLines(
                    CharStreams.newReaderSupplier(new InputSupplier<InputStream>() {
                        @Override
                        public InputStream getInput() throws IOException {
                            return urlConnection.getInputStream();
                        }
                    }, Charset.forName("UTF-8"))));
        } catch (IOException ioe) {
            LOG.error("Unable to read EC2 local metadata endpoint {}: ({}) {}",
                    EC2_LOCAL_METADATA_URL, ioe.getClass().getSimpleName(), ioe.getMessage());
        }
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    protected Result check() throws Exception {
        if (instanceId != null) {
            return Result.healthy(String.format("EC2 instance ID is %s.",instanceId));
        } else {
            return Result.unhealthy("Unable to determine EC2 instance ID.");
        }

    }
}
