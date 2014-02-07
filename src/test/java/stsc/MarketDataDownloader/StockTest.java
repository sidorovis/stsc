package stsc.MarketDataDownloader;

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

	public Stock readStockFromTestFile(String path, String name) throws IOException,
			ParseException {
		byte[] data = Files.readAllBytes(Paths.get(path));
		String content = new String(data);
		return Stock.newStockFromString(name, content);
	}

	public void testStockGenerate() throws IOException, ParseException {
		assertEquals(1, readStockFromTestFile("./test_data/anse.csv", "anse").getDays()
				.size());
		assertEquals(105, readStockFromTestFile("./test_data/aahc.csv", "aahc")
				.getDays().size());
		assertEquals(75, readStockFromTestFile("./test_data/aaoi.csv", "aaoi")
				.getDays().size());
		assertEquals(13098, readStockFromTestFile("./test_data/ibm.csv", "ibm")
				.getDays().size());
	}

	public void testStockStore() throws IOException, ParseException,
			ClassNotFoundException {
		{
			Stock s = readStockFromTestFile("./test_data/aaoi.csv", "aaoi");
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
		Stock aaoi = readStockFromTestFile("./test_data/aaoi.csv", "aaoi");
		assertEquals(
				"http://ichart.yahoo.com/table.csv?s=aaoi&a=0&b=14&c=2014",
				aaoi.generatePartiallyDownloadLine());
		Stock aahc = readStockFromTestFile("./test_data/aahc.csv", "aahc");
		assertEquals(
				"http://ichart.yahoo.com/table.csv?s=aahc&a=5&b=4&c=2013",
				aahc.generatePartiallyDownloadLine());
	}

	public void testAddDaysFromString() throws IOException, ParseException {
		Stock aaoi = readStockFromTestFile("./test_data/aaoi.csv", "aaoi");
		byte[] data = Files.readAllBytes(Paths.get("./test_data/aaoi_add.csv"));
		String content = new String(data);
		aaoi.addDaysFromString( content );
		assertEquals( 91, aaoi.getDays().size() );
	}
}
