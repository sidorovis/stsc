package stsc.frontend.zozka.components;

import java.io.File;

import stsc.common.storage.StockStorage;
import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestDatafeedLoader extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		final File f = new File("./test_data");
		final DatafeedLoader loader = new DatafeedLoader(stage, f);
		loader.startLoad(rh -> {
			final StockStorage stockStorage = loader.getStockStorage();
			System.out.println(rh);
			System.out.println(stockStorage);
		}, eh -> {
			System.out.println(eh);
		});

	}

	public static void main(String[] args) {
		Application.launch(VisualTestDatafeedLoader.class, args);
	}

}
