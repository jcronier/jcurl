package jcurl;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {

	private static final Logger LOG = LoggerFactory.getLogger(Version.class);

	private static final String FILE_VERSION = "META-INF/maven/jcurl/jcurl/pom.properties";

	public static final Version version = new Version();

	private Version() {
	}

	public void showVersion() {
		try {
			final Properties p = new Properties();
			p.load(ClassLoader.getSystemResourceAsStream(FILE_VERSION));

			final StringBuilder s = new StringBuilder();
			s.append(p.getProperty("artifactId"));
			s.append(" ");
			s.append(p.getProperty("version"));

			System.out.println(s);
		}
		catch (final IOException e) {
			LOG.error("Unable to find file version: '{}'", FILE_VERSION);
		}
	}
}