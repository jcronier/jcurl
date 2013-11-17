package jcurl;

import static jcurl.CliArgs.cliArgs;

import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cli {

	private static final Logger LOG = LoggerFactory.getLogger(Cli.class);

	private final String[] arguments;

	/**
	 * @param arguments
	 * @throws Exception
	 */
	private Cli(final String... arguments) throws Exception {
		this.arguments = arguments;
	}

	public static void main(final String... args) throws Exception {
		try {
			final int exitCode = new Cli(args).call();
			System.exit(exitCode);
		}
		catch (final MissingArgumentException e) {
		}
		catch (final MissingOptionException e) {
		}
		catch (final ParseException e) {
			LOG.error(e.getMessage(), e);
			System.exit(1);
			return;
		}
	}

	public Integer call() throws Exception {
		if (this.arguments.length == 0) {
			cliArgs.printUsage();
			return 0;
		}
		final CommandLineParser parser = new GnuParser();
		// create Options object
		try {
			final CommandLine cmd = parser.parse(cliArgs.getOptions(), this.arguments, true);
			if (cliArgs.isHelp(cmd)) {
				cliArgs.printUsage();
				return 0;
			}
			if (cmd.hasOption(CliArgs.MANUAL)) {
				cliArgs.printManual();
				return 0;
			}
			if (cmd.hasOption(CliArgs.VERSION)) {
				Version.version.showVersion();
				return 0;
			}
			final List<URL> urls;
			if (cmd.hasOption(CliArgs.INPUTFILE)) {
				final String inputFile = cmd.getOptionValue(CliArgs.INPUTFILE);
				final FileReader reader = new FileReader(inputFile);
				if (!cmd.hasOption(CliArgs.INPUTFILE_PATTERN)) {
					urls = UriUtils.uriUtils.getUrls(IOUtils.readLines(reader).toArray(new String[0]));
				}
				else {
					urls = UriUtils.uriUtils.getUrls( //
							reader //
							, Pattern.compile(cmd.getOptionValue(CliArgs.INPUTFILE_PATTERN)) //
							, cmd.getOptionValue(CliArgs.INPUTFILE_TRANSFORM) //
							);
				}
			}
			else {
				urls = UriUtils.uriUtils.getUrls(cmd.getArgs());
			}
			final Map<JcurlOption, String> opts = cliArgs.getJcurlOptions(cmd);
			final HttpMethod method = HttpMethod.valueOf(cmd.getOptionValue(CliArgs.VERB, "GET").toUpperCase());
			final Jcurl jcurl = new Jcurl(method, opts);
			for (final URL url : urls) {
				jcurl.run(url);
			}
		}
		catch (final Exception e) {
			System.err.println(e.getMessage());
			cliArgs.printUsage();
			throw e;
		}
		return 0;
	}
}