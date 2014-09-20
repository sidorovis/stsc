package stsc.frontend.zozka.settings;

public class DefaultSettingsControllerStringValue {
// @formatter:off
	public static String VALUE = 
		"StockExecutions = In, Sma" + "\n" +
		"In.loadLine = In(e = [high,low])" + "\n" +
		"Sma.loadLine = Sma(n = [5I,1I,15I], [In])" + "\n \n" +

		"EodExecutions = a1" + "\n" + 
		"a1.loadLine = PositionNDayMStocks( Sma, m = [1i, 1i, 3i], ps = [20000.0D,5000.0D,50000.0D] )" + "\n ";
		;
// @formatter:on
}
