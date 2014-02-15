package stsc.trading;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;

import stsc.algorithms.Algorithm;
import stsc.common.Day;
import stsc.storage.StockStorage;

public class MarketSimulator {

	private StockStorage stockStorage;
	private Broker broker;
	private Algorithm tradeAlgorithm;

	private Date from;
	private Date to;

	private List<String> processingStockList;

	public MarketSimulator() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			ParseException, IOException, InterruptedException {

		stockStorage = new StockStorage();
		broker = new Broker();

		Class<?> classType = Class.forName("stsc.algorithms.SimpleTraderExample");
		Constructor<?> constructor = classType.getConstructor();
		tradeAlgorithm = (Algorithm) constructor.newInstance();

		tradeAlgorithm.setBroker(broker);

		parseSimulationSettings();
	}

	private void parseSimulationSettings() throws ParseException {
		DateFormat dateReader = new SimpleDateFormat("dd-MM-yyyy");
		from = dateReader.parse("30-10-2013");
		to = dateReader.parse("10-02-2014");
		processingStockList = new ArrayList<String>();
		processingStockList.add("aapl");
		processingStockList.add("gfi");
	}

	public void simulate() {
		LocalDate dateIterator = new LocalDate( from );
		LocalDate endDate = new LocalDate( to );
		
	//	stockStorage.getStock(name);
		
		while (dateIterator.isBefore( endDate )){
			HashMap<String, Day> datafeed= new HashMap<String, Day>();
			
			tradeAlgorithm.process( dateIterator.toDate(), datafeed );
			dateIterator = dateIterator.plusDays(1);
		}
	}
}
