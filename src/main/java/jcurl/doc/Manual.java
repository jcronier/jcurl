package jcurl.doc;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

public class Manual {

	public static final Manual manual = new Manual();

	private Manual() {
	}

	public String getManual() {
		return null;
	}

	protected String toPlainText(final InputStream htmlInput) throws TransformerException {
		final TransformerFactory fact = TransformerFactory.newInstance();
		final Transformer tr = fact.newTransformer();
		final Source s;
		tr.transform(null, null);
		return null;
	}
}
