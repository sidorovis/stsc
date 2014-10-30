package stsc.frontend.zozka.applications;

import java.io.IOException;
import java.util.Date;


import org.controlsfx.dialog.Dialogs;


import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.controllers.PeriodAndDatafeedController;
import stsc.frontend.zozka.controllers.SimulatorSettingsController;
import stsc.frontend.zozka.panes.StrategiesPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ZozkaStrategySelector extends Application {

	private Stage owner;
	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();
	private final BorderPane chartPane = new BorderPane();

	private PeriodAndDatafeedController periodAndDatafeedController;
	private SimulatorSettingsController simulatorSettingsController;

	private void fillTopPart() throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedController(owner);
		simulatorSettingsController = new SimulatorSettingsController(owner);
		pane.setTop(periodAndDatafeedController.getGui());
		final SplitPane centerSplitPane = new SplitPane();
		centerSplitPane.getItems().add(simulatorSettingsController.getGui());
		centerSplitPane.getItems().add(chartPane);
		pane.setCenter(centerSplitPane);

		final HBox hbox = new HBox();

		final Button localGridSearchButton = new Button("Local Grid Search");
		localGridSearchButton.setOnAction(e -> {
			runLocalGridSearch();
		});

		final Button localGeneticSearchButton = new Button("Local Genetic Search");
		localGeneticSearchButton.setOnAction(e -> {
			Dialogs.create().owner(owner).showException(new Exception("Local Genetic Search Not Implemented Yet"));
		});

		final Button distributedGridSearchButton = new Button("Distributed Grid Search");
		distributedGridSearchButton.setOnAction(e -> {
			Dialogs.create().owner(owner).showException(new Exception("Distributed Grid Search Not Implemented Yet"));
		});
		final Button distributedGeneticSearchButton = new Button("Distributed Genetic Search");
		distributedGeneticSearchButton.setOnAction(e -> {
			Dialogs.create().owner(owner).showException(new Exception("Distributed Genetic Search Not Implemented Yet"));
		});

		hbox.getChildren().add(localGridSearchButton);
		hbox.getChildren().add(localGeneticSearchButton);
		hbox.getChildren().add(distributedGridSearchButton);
		hbox.getChildren().add(distributedGeneticSearchButton);

		hbox.setAlignment(Pos.CENTER);
		pane.setBottom(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER);
		splitPane.getItems().add(pane);
	}

	private void runLocalGridSearch() {
		periodAndDatafeedController.loadStockStorage(eh -> Platform.runLater(() -> {
			localGridSearch(periodAndDatafeedController.getStockStorage());
		}));
	}

	private void localGridSearch(StockStorage stockStorage) {
		if (stockStorage == null)
			return;
		final FromToPeriod period = periodAndDatafeedController.getPeriod();
		try {
			final StrategiesPane pane = new StrategiesPane(owner, period, simulatorSettingsController.getModel(), stockStorage, chartPane);
			final Tab tab = new Tab("Grid(" + (new Date()) + ")");
			tab.setContent(pane);
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);
		} catch (BadAlgorithmException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void fillBottomPart() {
		splitPane.getItems().add(tabPane);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.owner = stage;
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPosition(0, 0.1f);
		stage.setMinWidth(1200);
		stage.setMinHeight(800);
		fillTopPart();
		fillBottomPart();

		final Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(ZozkaStrategySelector.class, args);
	}

}
