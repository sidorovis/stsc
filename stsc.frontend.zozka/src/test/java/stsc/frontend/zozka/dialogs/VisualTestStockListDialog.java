package stsc.frontend.zozka.dialogs;

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
		for (String stockName : ss.getStockNames()) {
			dialog.getModel().add(new StockDescription(13, ss.getStock(stockName), true));
		}
		dialog.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockListDialog.class, (java.lang.String[]) null);
	}
}
