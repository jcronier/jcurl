package jcurl;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLACK;
import static org.fusesource.jansi.Ansi.Color.WHITE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

public class ResponseViewer {

	private static final String NL = System.getProperty("line.separator");

	private final Writer output;

	private final boolean displayHeader;

	private final boolean prettyPrint;

	public ResponseViewer(final OutputStreamWriter outputStreamWriter, final Map<JcurlOption, String> opts) {
		this.output = outputStreamWriter;
		this.displayHeader = opts.get(JcurlOption.displayHeader) != null;
		this.prettyPrint = opts.get(JcurlOption.prettyPrint) != null;
	}

	public void view(final HttpURLConnection connection) throws IOException {
		if (this.displayHeader) {
			if (this.prettyPrint) {
				this.print(ansi().bg(WHITE).fg(BLACK).toString());
			}
			this.printHeader(connection);
			if (this.prettyPrint) {
				this.print(ansi().reset().toString());
			}
		}
		if (this.prettyPrint) {
		}
		this.printBody(connection);
		this.output.flush();
	}

	public void printHeader(final HttpURLConnection connection) throws IOException {
		final StringBuilder out = new StringBuilder();
		for (final Entry<String, List<String>> e : connection.getHeaderFields().entrySet()) {
			final String key = e.getKey();
			if (key != null) {
				out.append(key).append(":").append(NL).append("\t");
			}
			final List<String> value = e.getValue();
			if ((value != null) && (value.size() == 1)) {
				out.append(value.get(0));
			}
			else {
				out.append(value);
			}
			out.append(NL);
		}
		this.print(out.toString());
	}

	public void printBody(final HttpURLConnection connection) throws IOException {
		InputStream input = null;
		try {
			input = connection.getInputStream();
			this.print(input);
		}
		finally {
			IOUtils.closeQuietly(input);
		}
	}

	public void print(final InputStream input) throws IOException {
		IOUtils.copy(input, this.output);
		this.output.flush();
	}

	public void print(final String input) throws IOException {
		IOUtils.write(input, this.output);
		this.output.flush();
	}
}