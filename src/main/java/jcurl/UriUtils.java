package jcurl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriUtils {

	private static final Logger LOG = LoggerFactory.getLogger(UriUtils.class);

	private static final String CODE_PLUS_D = "+";

	private static final String CODE_PLUS_E = "%20";

	private static final String CODE_SLASH_D = "/";

	private static final String CODE_SLASH_E = "%2F";

	private static final String CODE_STAR_D = "*";

	private static final String CODE_STAR_E = "%2A";

	private static final String CODE_DPOINT_D = ":";

	private static final String CODE_DPOINT_E = "%3A";

	private static final String CODE_QMARK_D = "?";

	private static final String CODE_QMARK_E = "%3F";

	private static final String CODE_AMP_D = "&";

	private static final String CODE_AMP_E = "%26";

	private static final String CODE_EQUAL_D = "=";

	private static final String CODE_EQUAL_E = "%3D";

	private static final String URL_ENCODING = "UTF-8";

	public static final UriUtils uriUtils = new UriUtils();

	private final Pattern PATTERN_LIST = Pattern.compile("\\{([^}]+)\\}");

	private final Pattern PATTERN_RANGE_CAR = Pattern.compile("\\[([^\\]])-([^\\]])(:(\\d+))?\\]");

	private final Pattern PATTERN_RANGE_NUMBER = Pattern.compile("\\[(\\d+)-(\\d+)(:(\\d+))?\\]");

	private UriUtils() {
	}

	public String decode(final String input) {
		return this.decode(input, null);
	}

	public String decode(final String input, final String defaultResultValue) {
		if (input == null) {
			return defaultResultValue;
		}
		try {
			return URLDecoder.decode(input, URL_ENCODING);
		}
		catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String encode(final String input) {
		return this.encode(input, true, null);
	}

	/**
	 * @param input
	 * @param defaultValue
	 *          Valeur par défaut pris en compte dans le cas où <code>input</code>
	 *          serait <code>null</code>.
	 * @return
	 */
	public String encode(final String input, final boolean urlPart, final String defaultResultValue) {
		if (input == null) {
			return defaultResultValue;
		}
		try {
			String result = URLEncoder.encode(input, URL_ENCODING);
			result = result //
					.replace(CODE_STAR_D, CODE_STAR_E) //
					.replace(CODE_PLUS_D, CODE_PLUS_E) //
			;
			if (!urlPart) {
				result = result.replace(CODE_SLASH_E, CODE_SLASH_D);
				result = result.replace(CODE_DPOINT_E, CODE_DPOINT_D);
				result = result.replace(CODE_QMARK_E, CODE_QMARK_D);
				result = result.replace(CODE_AMP_E, CODE_AMP_D);
				result = result.replace(CODE_EQUAL_E, CODE_EQUAL_D);
			}
			return result;
		}
		catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String encodeCompleteUrl(final String input) {
		return this.encode(input, false, null);
	}

	public String encodeCompleteUrl(final String input, final String defaultResultValue) {
		return this.encode(input, false, defaultResultValue);
	}

	public Object encode(final String input, final String defaultResultValue) {
		return this.encode(input, true, defaultResultValue);
	}

	protected List<URL> getUrls(final String... urlStrings) throws MalformedURLException {
		final List<URL> result = new ArrayList<URL>();
		for (final String urlStr : urlStrings) {
			Matcher m = this.PATTERN_RANGE_CAR.matcher(urlStr);
			if (m.find()) {
				final char start = m.group(1).charAt(0);
				final char end = m.group(2).charAt(0);
				int step = 1;
				if ((m.groupCount() > 3) && (m.group(4) != null)) {
					step = Integer.parseInt(m.group(4));
				}
				for (char i = start; i <= end; i += step) {
					result.addAll(this.getUrls(m.replaceFirst(i + "")));
				}
				continue;
			}
			m = this.PATTERN_RANGE_NUMBER.matcher(urlStr);
			if (m.find()) {
				final String startStr = m.group(1);
				final long start = Long.parseLong(m.group(1));
				final long end = Long.parseLong(m.group(2));
				int step = 1;
				if ((m.groupCount() > 3) && (m.group(4) != null)) {
					step = Integer.parseInt(m.group(4));
				}
				final int leading = ((startStr.length() > 1) && (startStr.length() == m.group(2).length()) && startStr.startsWith("0")) ? startStr.length()
						: 0;
				if (leading > 0) {
					for (long i = start; i <= end; i += step) {
						final String subst = StringUtils.leftPad(String.valueOf(i), leading, "0");
						result.addAll(this.getUrls(m.replaceFirst(subst)));
					}
				}
				else {
					for (long i = start; i <= end; ++i) {
						result.addAll(this.getUrls(m.replaceFirst(String.valueOf(i))));
					}
				}
				continue;
			}
			m = this.PATTERN_LIST.matcher(urlStr);
			if (m.find()) {
				final String[] list = m.group(1).split(",");
				for (final String listItem : list) {
					result.addAll(this.getUrls(m.replaceFirst(listItem)));
				}
				continue;
			}
			final String urlEncoded = UriUtils.uriUtils.encodeCompleteUrl(urlStr);
			try {
				final URL u = new URL(urlEncoded);
				result.add(u);
			}
			catch (final MalformedURLException e) {
				LOG.error("URL '{}' not valid", urlEncoded);
			}
		}
		return result;
	}

	protected List<URL> getUrls(final Reader input, final Pattern pattern, final String replace) throws IOException {
		final List<URL> result = new ArrayList<URL>();
		if (input == null) {
			return result;
		}
		final BufferedReader reader = new BufferedReader(input);
		String line;
		while ((line = reader.readLine()) != null) {
			result.addAll(this.getUrls(line, pattern, replace));
		}
		return result;
	}

	protected List<URL> getUrls(final String lineStr, final Pattern pattern, final String replace) throws IOException {
		final List<URL> result = new ArrayList<URL>();
		final String line;
		final Matcher m = pattern.matcher(lineStr);
		if (!m.find()) {
			return result;
		}
		if (pattern != null) {
			line = m.replaceAll(replace);
		}
		else {
			line = lineStr;
		}
		for (final URL u : this.getUrls(line)) {
			result.add(u);
		}
		return result;
	}
}