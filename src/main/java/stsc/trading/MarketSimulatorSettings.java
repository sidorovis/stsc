package stsc.trading;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import stsc.algorithms.EodExecution;
import stsc.algorithms.StockExecution;
import stsc.storage.StockStorage;

public class MarketSimulatorSettings {

	private StockStorage stockStorage;
	private Broker broker;

	private DateFormat dateReader = new SimpleDateFormat("dd-MM-yyyy");

	private Date from;
	private Date to;

	private ArrayList<StockExecution> executionsStockList = new ArrayList<>();
	private ArrayList<EodExecution> executionsEodList = new ArrayList<>();

	private ArrayList<String> stockList = new ArrayList<String>();

	public ArrayList<String> getStockList() {
		return stockList;
	}

	public void setFrom(String from) throws ParseException {
		this.from = dateReader.parse(from);
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public void setTo(String to) throws ParseException {
		this.to = dateReader.parse(to);
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

	public ArrayList<StockExecution> getStockExecutionsList() {
		return executionsStockList;
	}

	public ArrayList<EodExecution> getEodExecutionsList() {
		return executionsEodList;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	public void setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
}
