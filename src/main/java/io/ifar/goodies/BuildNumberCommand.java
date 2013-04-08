package io.ifar.goodies;

import com.yammer.dropwizard.cli.Command;
import com.yammer.dropwizard.config.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 * Output the Git SHA hash.
 */
public class BuildNumberCommand extends Command {
    private final static String MANIFEST = "META-INF/MANIFEST.MF";
    private final static String BUILD_VERSION = "Implementation-Build";


    public BuildNumberCommand() {
        super("build-number", "Show build number");
    }

    @Override
    public void configure(Subparser subparser) {
        // no op
    }

    @Override
    public void run(Bootstrap bootstrap, Namespace namespace) throws Exception
    {
        Either<String,String> result = getBuildVersion();
        if (result.isLeft())
            System.out.println(result.left());
        else {
            System.err.println(result.right());
            System.exit(1);
        }
    }

    public static String getBuildOrError() {
        Either<String,String> result = getBuildVersion();
        if (result.isLeft())
            return result.left();
        else
            return result.right();
    }

    /**
     * Return build number as indicated by git SHA embedded in JAR MANIFEST.MF
     * @return Either version number String or error message String
     */
    public static Either<String,String> getBuildVersion() {
        //Find the right manifest from the many on classpath. See http://stackoverflow.com/questions/1272648/reading-my-own-jars-manifest
        Class clazz = BuildNumberCommand.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return Either.right("Cannot determine build version when not running from JAR.");
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf('!') + 1) + "/META-INF/MANIFEST.MF";
        try {
            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
            String build = manifest.getMainAttributes().getValue(BUILD_VERSION);
            if (build == null) {
                return Either.right(String.format("Cannot determine build version when %s missing from JAR manifest %s", BUILD_VERSION, MANIFEST));
            } else {
                return Either.left(build);
            }
        } catch (IOException e) {
            return Either.right(String.format("Cannot access %s: %s", manifestPath, e));
        }
    }
}

