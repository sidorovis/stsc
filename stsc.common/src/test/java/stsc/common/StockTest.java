package stsc.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;

public class StockTest {

	@Test
	public void testStockGenerate() throws IOException, ParseException {
		Assert.assertEquals(1, UnitedFormatStock.readFromCsvFile("anse", "./test_data/anse.csv").getDays().size());
		Assert.assertEquals(105, UnitedFormatStock.readFromCsvFile("aahc", "./test_data/aahc.csv").getDays().size());
		Assert.assertEquals(75, UnitedFormatStock.readFromCsvFile("aaoi", "./test_data/aaoi.csv").getDays().size());
		Assert.assertEquals(13098, UnitedFormatStock.readFromCsvFile("ibm", "./test_data/ibm.csv").getDays().size());
	}

	@Test
	public void testGeneratePartiallyDownloadLine() throws IOException, ParseException {
		UnitedFormatStock aaoi = UnitedFormatStock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
		Assert.assertEquals("http://ichart.yahoo.com/table.csv?s=aaoi&a=0&b=14&c=2014", aaoi.generatePartiallyDownloadLine());
		UnitedFormatStock aahc = UnitedFormatStock.readFromCsvFile("aahc", "./test_data/aahc.csv");
		Assert.assertEquals("http://ichart.yahoo.com/table.csv?s=aahc&a=5&b=4&c=2013", aahc.generatePartiallyDownloadLine());
	}

	@Test
	public void testAddDaysFromString() throws IOException, ParseException {
		UnitedFormatStock aaoi = UnitedFormatStock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
		byte[] data = Files.readAllBytes(Paths.get("./test_data/aaoi_add.csv"));
		String content = new String(data);
		aaoi.addDaysFromString(content);
		Assert.assertEquals(91, aaoi.getDays().size());
	}

	@Test
	public void testUniteFormat() throws IOException, ParseException {
		UnitedFormatStock s = UnitedFormatStock.readFromCsvFile("aaoi", "./test_data/aaoi.csv");
		s.storeUniteFormat("./test/aaoi.uf");
		Stock s_copy = UnitedFormatStock.readFromUniteFormatFile("./test/aaoi.uf");
		Assert.assertEquals("aaoi", s_copy.getName());
		new File("./test/aaoi.uf").delete();
		Assert.assertEquals(75, s_copy.getDays().size());
		Assert.assertEquals(75, s.getDays().size());
	}
}
