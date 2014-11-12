package stsc.frontend.zozka.panes;

import org.controlsfx.dialog.Dialogs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualTestStockDatafeedListPane extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		StockDatafeedListPane mainPane = new StockDatafeedListPane(parent, "<datafeed title>");
		final Scene scene = new Scene(mainPane);
		parent.setScene(scene);
		parent.show();
		mainPane.loadDatafeed("./test_data/data", () -> {
			Dialogs.create().title("Finished").masthead(null).message("DownloadFinished").showInformation();
		});
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockDatafeedListPane.class, (java.lang.String[]) null);
	}

}
