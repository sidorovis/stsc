package stsc.frontend.zozka.dialogs;

import java.util.Optional;

import stsc.common.stocks.Stock;
import stsc.frontend.zozka.models.StockDescription;
import stsc.yahoo.YahooFileStockStorage;
import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestStockListDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final YahooFileStockStorage ss = new YahooFileStockStorage("./test_data/data", "./test_data/filtered_data");
		ss.waitForLoad();
		final StockListDialog dialog = new StockListDialog(parent, "StockList");
		int index = 0;
		for (String stockName : ss.getStockNames()) {
			final Optional<Stock> stock = ss.getStock(stockName);
			if (stock.isPresent()) {
				dialog.getModel().add(new StockDescription(index++, stock.get()));
			}
		}
		dialog.setOnMouseDoubleClicked(stockDescription -> {
			new TextAreaDialog(parent, "Temp Dialog", stockDescription.toString()).show();
			return Optional.empty();
		});
		dialog.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockListDialog.class, (java.lang.String[]) null);
	}
}
