package jcurl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

public class UriUtilsTest {

	@Test
	public void testGetUrls_rangeNumber() throws MalformedURLException {
		assertEquals(Arrays.asList( //
				new URL("http://www.numericals.com/file6.txt") //
				, new URL("http://www.numericals.com/file7.txt") //
				, new URL("http://www.numericals.com/file8.txt") //
				, new URL("http://www.numericals.com/file9.txt") //
				, new URL("http://www.numericals.com/file10.txt") //
				, new URL("http://www.numericals.com/file11.txt") //
				), UriUtils.uriUtils.getUrls("http://www.numericals.com/file[6-11].txt"));
	}

	@Test
	public void testGetUrls_rangeAlpha() throws MalformedURLException {
		assertEquals(Arrays.asList( //
				new URL("http://www.letters.com/file_b.txt") //
				, new URL("http://www.letters.com/file_c.txt") //
				, new URL("http://www.letters.com/file_d.txt") //
				, new URL("http://www.letters.com/file_e.txt") //
				, new URL("http://www.letters.com/file_f.txt") //
				), UriUtils.uriUtils.getUrls("http://www.letters.com/file_[b-f].txt"));
		assertEquals("with step", Arrays.asList( //
				new URL("http://www.letters.com/file_b.txt") //
				, new URL("http://www.letters.com/file_e.txt") //
				), UriUtils.uriUtils.getUrls("http://www.letters.com/file_[b-f:3].txt"));
	}

	@Test
	public void testGetUrls_list() throws MalformedURLException {
		assertEquals(Arrays.asList( //
				new URL("http://www.lists.com/file_OKAJS9Z.txt") //
				, new URL("http://www.lists.com/file_ADOLIA8.txt") //
				, new URL("http://www.lists.com/file_087YJA9.txt") //
				, new URL("http://www.lists.com/file_0987UJH.txt") //
				, new URL("http://www.lists.com/file_AUDYAI9.txt") //
				), UriUtils.uriUtils.getUrls("http://www.lists.com/file_{OKAJS9Z,ADOLIA8,087YJA9,0987UJH,AUDYAI9}.txt"));
	}

	@Test
	public void testGetUrls_rangeWithPadding() throws MalformedURLException {
		assertEquals(Arrays.asList( //
				new URL("http://www.numericals.com/file_006.txt") //
				, new URL("http://www.numericals.com/file_007.txt") //
				, new URL("http://www.numericals.com/file_008.txt") //
				, new URL("http://www.numericals.com/file_009.txt") //
				, new URL("http://www.numericals.com/file_010.txt") //
				, new URL("http://www.numericals.com/file_011.txt") //
				), UriUtils.uriUtils.getUrls("http://www.numericals.com/file_[006-011].txt"));
		assertEquals(Arrays.asList( //
				new URL("http://www.numericals.com/file_006.txt") //
				, new URL("http://www.numericals.com/file_008.txt") //
				, new URL("http://www.numericals.com/file_010.txt") //
				), UriUtils.uriUtils.getUrls("http://www.numericals.com/file_0[06-11:2].txt"));
	}

	@Test
	public void testGetUrls() throws MalformedURLException {
		assertEquals(Arrays.asList( //
				new URL("http://any.org/archive_1996/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1996/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1996/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1996/vol-03/part-intro.html") //
				, new URL("http://any.org/archive_1996/vol-03/part-main.html") //
				, new URL("http://any.org/archive_1996/vol-03/part-end.html") //
				, new URL("http://any.org/archive_1996/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1996/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1996/vol-05/part-end.html") //
				, new URL("http://any.org/archive_1997/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1997/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1997/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1997/vol-03/part-intro.html") //
				, new URL("http://any.org/archive_1997/vol-03/part-main.html") //
				, new URL("http://any.org/archive_1997/vol-03/part-end.html") //
				, new URL("http://any.org/archive_1997/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1997/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1997/vol-05/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-03/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-03/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-03/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-end.html") //
				), UriUtils.uriUtils.getUrls("http://any.org/archive_[1996-1998]/vol-[01-06:2]/part-{intro,main,end}.html"));
	}

	@Test
	public void testGetRequests() throws IOException {
		final StringReader input = new StringReader( //
				"Year	Vol	Part" //
						+ "\n1996	01	intro" //
						+ "\n1996	01	main" //
						+ "\n1996	01	end" //
						+ "\n1996	03	intro" //
						+ "\n1996	03	main" //
						+ "\n1996	03	end" //
						+ "\n1996	05	intro" //
						+ "\n1996	05	main" //
						+ "\n1996	05	end" //
						+ "\n1997	01	intro" //
						+ "\n1997	01	main" //
						+ "\n1997	01	end" //
						+ "\n1997	03	intro" //
						+ "\n1997	03	main" //
						+ "\n1997	03	end" //
						+ "\n1997	05	intro" //
						+ "\n1997	05	main" //
						+ "\n1997	05	end" //
						+ "\n1998	01	intro" //
						+ "\n1998	01	main" //
						+ "\n1998	01	end" //
						+ "\n1998	03	intro" //
						+ "\n1998	03	main" //
						+ "\n1998	03	end" //
						+ "\n1998	05	intro" //
						+ "\n1998	05	main" //
						+ "\n1998	05	end" //
		);
		final List<URL> expected = Arrays.asList( //
				new URL("http://any.org/archive_1998/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-01/part-end.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-intro.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-main.html") //
				, new URL("http://any.org/archive_1998/vol-05/part-end.html") //
				);
		final List<URL> result = UriUtils.uriUtils.getUrls( //
				input //
				, Pattern.compile("^[^\\t]+\\t(0[15]+)\\t([^\\t]+)$") //
				, "http://any.org/archive_1998/vol-$1/part-$2.html" //
		);
		assertEquals(18, result.size());
		assertEquals(expected, result);
	}

	@Test
	public void testGetRequestsFromLine() throws MalformedURLException, IOException {
		assertEquals( //
				Arrays.asList(new URL("http://any.org/archive_1998/vol-05/part-end.html")) //
				, UriUtils.uriUtils.getUrls( //
						"1996	05	end" //
						, Pattern.compile("^[^\\t]+\\t([^\\t]+)\\t([^\\t]+)$") //
						, "http://any.org/archive_1998/vol-$1/part-$2.html" //
				));
	}

	@Test
	public void testEncodeCompleteUrlString() {
		final String input = "http://10.111.228.28:8082/crtype/create?crTypeName=TEST&description=Test type avec jcurl (description avec espace et caractères spéciaux)";
		final String expected = "http://10.111.228.28:8082/crtype/create?crTypeName=TEST&description=Test%20type%20avec%20jcurl%20%28description%20avec%20espace%20et%20caract%C3%A8res%20sp%C3%A9ciaux%29";
		assertEquals(expected, UriUtils.uriUtils.encodeCompleteUrl(input));
	}
}
