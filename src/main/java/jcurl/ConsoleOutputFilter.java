package jcurl;

import java.io.BufferedReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleOutputFilter extends FilterOutputStream {

	private final String encoding;

	private final boolean toTranscode;

	public ConsoleOutputFilter(final OutputStream out, final String encoding) {
		super(out);
		this.encoding = getOutputEncoding(encoding);
		this.toTranscode = !Charset.defaultCharset().name().equals(this.encoding);
	}

	public ConsoleOutputFilter(final OutputStream out) {
		this(out, getOutputEncoding(null));
	}

	private final List<Byte> buffer = new ArrayList<Byte>();

	@Override
	public void write(final int b) throws IOException {
		if (this.toTranscode) {
			this.buffer.add((byte) b);
			if (b == 13) {
				this.flush();
			}
		}
		else {
			super.write(b);
		}
	}

	@Override
	public void flush() throws IOException {
		if (this.toTranscode) {
			final byte[] o = (new String(toArray(this.buffer)).getBytes(this.encoding));
			for (final byte element : o) {
				super.write(element);
			}
			this.buffer.clear();
		}
		super.flush();
	}

	/**
	 * Copies a collection of {@code Byte} instances into a new array of primitive
	 * {@code byte} values.
	 * 
	 * <p>
	 * Elements are copied from the argument collection as if by
	 * {@code collection.toArray()}. Calling this method is as thread-safe as
	 * calling that method.
	 * 
	 * @param collection
	 *          a collection of {@code Byte} objects
	 * @return an array containing the same values as {@code collection}, in the
	 *         same order, converted to primitives
	 * @throws NullPointerException
	 *           if {@code collection} or any of its elements is null
	 */
	public static byte[] toArray(final Collection<Byte> collection) {
		final Object[] boxedArray = collection.toArray();
		final int len = boxedArray.length;
		final byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Byte) boxedArray[i];
		}
		return array;
	}

	public static String getOutputEncoding(String defaultEncoding) {
		if (defaultEncoding == null) {
			defaultEncoding = Charset.defaultCharset().name();
		}
		final String lang = System.getenv("LANG");
		if (lang != null) {
			final Pattern p = Pattern.compile("^.._..\\.(.+?)$");
			final Matcher m = p.matcher(lang);
			if (m.find()) {
				return m.group(1);
			}
		}
		if (System.getenv("SHELL") == null) {
			// pour prévenir d'une exécution via CYGWIN
			BufferedReader br = null;
			try {
				final Process p = Runtime.getRuntime().exec("cmd /c chcp");
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				final String l = br.readLine();
				if (l != null) {
					final int cp = Integer.parseInt(l.replaceFirst("^.+?:\\s*(\\d+)\\s*$", "$1"));
					return "cp" + cp;
				}
				br.close();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
			catch (final NumberFormatException e) {
				e.printStackTrace();
			}
			finally {
				if (br != null) {
					try {
						br.close();
					}
					catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return defaultEncoding;
	}

}
