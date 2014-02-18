package stsc.trading;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TradingLog {
	public enum TradingType {
		BUY, SELL
	};

	public static class TradingRecord {
		private static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		TradingType type;
		Date when;
		String stockName;
		Side side;
		int amount;

		public static TradingRecord buy(Date when, String stockName, Side side, int amount) {
			return new TradingRecord(TradingType.BUY, when, stockName, side, amount);
		}

		public static TradingRecord sell(Date when, String stockName, Side side, int amount) {
			return new TradingRecord(TradingType.SELL, when, stockName, side, amount);
		}

		private TradingRecord(TradingType type, Date when, String stockName, Side side, int amount) {
			this.type = type;
			this.when = when;
			this.stockName = stockName;
			this.side = side;
			this.amount = amount;
		}

		public void printOut(Writer w) throws IOException {
			w.append(dateFormat.format(when));
			w.append("\t");
			if (type == TradingType.BUY)
				w.append("B");
			else
				w.append("S");
			w.append("\t").append(stockName).append("\t");
			if (side == Side.LONG)
				w.append("LONG");
			else
				w.append("SHORT");
			w.append("\t");
			w.write(new Integer(amount).toString());
			w.append("\n");
		}
	}

	private ArrayList<TradingRecord> records = new ArrayList<TradingRecord>();

	void addBuyRecord(Date when, String stockName, Side side, int sharesAmount) {
		records.add(TradingRecord.buy(when, stockName, side, sharesAmount));
	}

	void addSellRecord(Date when, String stockName, Side side, int sharesAmount) {
		records.add(TradingRecord.sell(when, stockName, side, sharesAmount));
	}

	void printOut(Writer w) throws IOException {
		for (TradingRecord record : records) {
			record.printOut(w);
		}
	}
}
