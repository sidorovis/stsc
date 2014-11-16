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
		int index = 0;
		for (String stockName : ss.getStockNames()) {
			dialog.getModel().add(new StockDescription(index++, ss.getStock(stockName), index % 3 == 0, index % 2 == 0));
		}
		dialog.setOnMouseDoubleClicked(stockDescription -> {
			new TextAreaDialog(parent, "Temp Dialog", stockDescription.toString()).show();
			return null;
		});
		dialog.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockListDialog.class, (java.lang.String[]) null);
	}
}
