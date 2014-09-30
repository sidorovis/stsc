package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;

import stsc.common.Day;
import stsc.common.stocks.Stock;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PresimulationCheckController implements Initializable {

	private SwingNode sn = new SwingNode();
	private Stage stage;
	private SimulationsDescription simulationsDescription;

	private DatasetForStock chartDataset;

	@FXML
	private BorderPane centralPane;

	PresimulationCheckController(final Stage stage, SimulationsDescription simulationsDescription) throws IOException {
		this.stage = new Stage();
		this.simulationsDescription = simulationsDescription;
		final URL location = Zozka.class.getResource("02_presimulation_check.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		this.stage.initOwner(stage);
		this.stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		this.stage.setScene(scene);
		this.stage.setMinHeight(480);
		this.stage.setMinWidth(640);
		this.stage.setTitle("Presimulation Check");
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert centralPane != null : "fx:id=\"centralPane\" was not injected: check your FXML file.";
		// centralPane;
		final Stock aapl = simulationsDescription.getStockStorage().getStock("aapl");
		chartDataset = new DatasetForStock(aapl);

		JFreeChart chart = ChartFactory.createCandlestickChart("Title", "", "", chartDataset, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		sn.setContent(chartPanel);
		centralPane.setCenter(sn);
	}
}
