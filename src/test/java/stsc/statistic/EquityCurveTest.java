package stsc.statistic;

import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class EquityCurveTest extends TestCase {
	public void testEquityCurve() {
		TradingLog tradingLog = new TradingLog();
		EquityCurve equityCurve = new EquityCurve(tradingLog);
	}
}
