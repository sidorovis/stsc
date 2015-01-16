package stsc.frontend.zozka.panes;

import java.util.Optional;
import java.util.function.Function;

import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.dialogs.TextAreaDialog;
import stsc.frontend.zozka.models.StockDescription;
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
		mainPane.loadDatafeed("./test_data/data", f -> {
			Dialogs.create().title("Finished").masthead(null).message("Download Finished").showInformation();
			return null;
		}, null);
		mainPane.setOnMouseDoubleClick(new Function<StockDescription, Optional<Void>>() {
			@Override
			public Optional<Void> apply(StockDescription sd) {
				new TextAreaDialog(parent, sd.getStock().getName(), sd.toString()).show();
				return Optional.empty();
			}
		});
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStockDatafeedListPane.class, (java.lang.String[]) null);
	}

}
