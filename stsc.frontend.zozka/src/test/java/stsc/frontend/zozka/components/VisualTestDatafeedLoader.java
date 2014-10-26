package stsc.frontend.zozka.components;

import java.io.File;

import stsc.common.storage.StockStorage;
import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestDatafeedLoader extends Application {

	private void load(Stage stage, File f) throws Exception {
		final DatafeedLoader loader = new DatafeedLoader(stage, f);
		loader.startLoad(rh -> {
			StockStorage stockStorage;
			try {
				stockStorage = loader.getStockStorage();
				System.out.println(rh);
				System.out.println(stockStorage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, eh -> {
			System.out.println(eh);
		});
	}

	@Override
	public void start(Stage stage) throws Exception {
		final File f = new File("./test_data");
		for (int i = 0; i < 10; ++i) {
			load(stage, f);
		}
	}

	public static void main(String[] args) {
		Application.launch(VisualTestDatafeedLoader.class, args);
	}

}
