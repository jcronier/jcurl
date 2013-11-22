package jcurl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliArgs {

	private static final Logger LOG = LoggerFactory.getLogger(CliArgs.class);

	static final String DISPLAYHEADER = "i";

	static final String HELP = "h";

	static final String MANUAL = "manual";

	static final String PPRINT = "pp";

	static final String VERB = "X";

	static final String VERSION = "V";

	static final String INPUTFILE = "inputFile";

	static final String INPUTFILE_PATTERN = "inputPattern";

	static final String INPUTFILE_TRANSFORM = "inputTransform";

	public static final CliArgs cliArgs = new CliArgs();

	private final ResourceBundle i18n;

	private final Options options;

	private CliArgs() {
		this.i18n = ResourceBundle.getBundle(Cli.class.getName());
		this.options = new Options();
		options.addOption(this.createOption(HELP));
		options.addOption(this.createOption(VERSION));
		options.addOption(this.createOption(MANUAL));
		// groupInfo.addOption(this.createOption(PPRINT));
		options.addOption(this.createOption(DISPLAYHEADER));
		options.addOption(this.createArgOption(INPUTFILE));
		options.addOption(this.createArgOption(INPUTFILE_PATTERN));
		options.addOption(this.createArgOption(INPUTFILE_TRANSFORM));
		options.addOption(this.createArgOption(VERB));
	}

	protected Options getOptions() {
		return this.options;
	}

	private Option createArgOption(final String param) {
		return this.createOption(param, true);
	}

	protected Option createOption(final String param) {
		return this.createOption(param, false);
	}

	protected Option createOption(final String param, final boolean hasArg) {
		final String description = this.getString("param." + param + ".description", null);
		final String longOpt = this.getString("param." + param + ".long", null);
		final String argName = this.getString("param." + param + ".argname", null);
		if (hasArg) {
			OptionBuilder.hasArg();
		}
		if (description != null) {
			OptionBuilder.withDescription(description);
		}
		if (longOpt != null) {
			OptionBuilder.withLongOpt(longOpt);
		}
		if (argName != null) {
			OptionBuilder.withArgName(argName);
		}
		return OptionBuilder.create(param);
	}

	private int getInt(final String key, final int defaultValue) {
		try {
			final String str = this.i18n.getString(key);
			return Integer.parseInt(str);
		}
		catch (final MissingResourceException e) {
			return defaultValue;
		}
		catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	private String getString(final String key, final String defaultValue) {
		try {
			return this.i18n.getString(key);
		}
		catch (final MissingResourceException e) {
			return defaultValue;
		}
	}

	public void printManual() {
		final String manualFile = this.getString("manual", null);
		try {
			final InputStream manual = ClassLoader.getSystemResourceAsStream(manualFile);
			if (manual == null) {
				throw new FileNotFoundException("Unable to find '" + manualFile + "'");
			}
			AnsiConsole.systemInstall();
			final String manualContent = IOUtils.toString(manual);
			System.out.println(Ansi.ansi().eraseScreen().render(manualContent).reset());
		}
		catch (final IOException e) {
			LOG.error("Unable du display help usage", e);
		}
	}

	public void printUsage() {
		final Options options = new Options();
		for (final Option o : (Collection<Option>) cliArgs.getOptions().getOptions()) {
			options.addOption(o);
		}
		final HelpFormatter formatter = new HelpFormatter();
		// formatter.setLeftPadding(4);
		// formatter.setWidth(80);
		// formatter.printHelp("java -jar gpcc-core.jar", "\n", options,
		// StringUtils.join(additionnalInformations, "\n"));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final PrintWriter pw = new PrintWriter(out);

		formatter.printHelp(pw, 80 //
				, this.getString("usage.cmdLineSyntax", "") //
				, this.getString("usage.header", "") //
				, options //
				, this.getInt("usage.leftPad", 1) //
				, this.getInt("usage.descPad", 1) //
				, this.getString("usage.footer", "") //
				);
		pw.flush();
		System.out.println(out.toString());
	}

	public boolean isHelp(final CommandLine cmd) {
		return ((cmd.getOptions().length == 0) && (cmd.getArgs().length == 0)) || cmd.hasOption(CliArgs.HELP);
	}

	public Map<JcurlOption, String> getJcurlOptions(final CommandLine cmd) {
		final Map<JcurlOption, String> opts = new LinkedHashMap<JcurlOption, String>();
		if (cmd.hasOption(CliArgs.PPRINT)) {
			opts.put(JcurlOption.prettyPrint, "true");
		}
		return opts;
	}
}