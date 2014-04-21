package stsc.trading;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

public class TradingLog {
	private ArrayList<TradingRecord> records = new ArrayList<TradingRecord>();

	public void addBuyRecord(Date when, String stockName, Side side, int sharesAmount) {
		records.add(TradingRecord.buy(when, stockName, side, sharesAmount));
	}

	public void addSellRecord(Date when, String stockName, Side side, int sharesAmount) {
		records.add(TradingRecord.sell(when, stockName, side, sharesAmount));
	}

	public void printOut(Writer w) throws IOException {
		for (TradingRecord record : records) {
			record.printOut(w);
		}
	}

	public ArrayList<TradingRecord> getRecords() {
		return records;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TradingRecord i : records) {
			sb.append(i.toString()).append("\n");
		}
		return sb.toString();
	}

}
