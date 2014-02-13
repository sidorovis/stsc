package stsc.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import junit.framework.TestCase;

public class StockTest extends TestCase {

	public void testStockGenerate() throws IOException, ParseException {
		assertEquals(1, Stock.readFromCsvFile("anse", "./test_data/anse.csv")
				.getDays().size());
		assertEquals(105, Stock.readFromCsvFile("aahc", "./test_data/aahc.csv")
				.getDays().size());
		assertEquals(75, Stock.readFromCsvFile("aaoi", "./test_data/aaoi.csv")
				.getDays().size());
		assertEquals(13098, Stock.readFromCsvFile("ibm", "./test_data/ibm.csv")
				.getDays().size());
	}

	public void testStockStore() throws IOException, ParseException,
			ClassNotFoundException {
		{
			Stock s = Stock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					"./test/aaoi.bin"));
			ObjectOutput oo = new ObjectOutputStream(os);
			oo.writeObject(s);
			oo.close();
		}
		{
			InputStream is = new BufferedInputStream(new FileInputStream(
					"./test/aaoi.bin"));
			ObjectInput oi = new ObjectInputStream(is);
			Stock s = null;
			s = (Stock) oi.readObject();
			oi.close();
			assertEquals(75, s.getDays().size());
		}
		(new File("./test/aaoi.bin")).delete();
	}

	public void testGeneratePartiallyDownloadLine() throws IOException,
			ParseException {
		Stock aaoi = Stock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
		assertEquals(
				"http://ichart.yahoo.com/table.csv?s=aaoi&a=0&b=14&c=2014",
				aaoi.generatePartiallyDownloadLine());
		Stock aahc = Stock.readFromCsvFile("aahc", "./test_data/aahc.csv");
		assertEquals("http://ichart.yahoo.com/table.csv?s=aahc&a=5&b=4&c=2013",
				aahc.generatePartiallyDownloadLine());
	}

	public void testAddDaysFromString() throws IOException, ParseException {
		Stock aaoi = Stock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
		byte[] data = Files.readAllBytes(Paths.get("./test_data/aaoi_add.csv"));
		String content = new String(data);
		aaoi.addDaysFromString(content);
		assertEquals(91, aaoi.getDays().size());
	}
}
