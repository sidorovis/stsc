package stsc.frontend.zozka.panes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stsc.common.FromToPeriod;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import stsc.yahoo.YahooFileStockStorage;

public class VisualTestStockViewPane extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final YahooFileStockStorage yfss = new YahooFileStockStorage("./test_data/data", "./test_data/filtered_data");
		yfss.waitForLoad();
		final Stock aapl = yfss.getStock("aapl");
		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2010");

		final TradeProcessorInit init = new TradeProcessorInit(yfss, period,
				"EodExecutions = a1\na1.loadLine = OpenWhileSignalAlgorithm( Level( f = 0.75d, Diff(In(e=close), In(e=open)) ) )\n");
		final List<String> executionsName = init.generateOutForStocks();
		final SimulatorSettings settings = new SimulatorSettings(0, init);

		final Set<String> stockNames = new HashSet<String>(Arrays.asList(new String[] { "aapl" }));
		final Simulator simulator = new Simulator(settings, stockNames);
		final SignalsStorage signalsStorage = simulator.getSignalsStorage();

		final StockViewPane stockViewPane = new StockViewPane(parent, aapl, period, executionsName, signalsStorage);

		final Scene scene = new Scene(stockViewPane.getMainPane());
		parent.setScene(scene);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockViewPane.class, (java.lang.String[]) null);
	}

}
