package stsc.frontend.zozka.panes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.dialogs.DatePickerDialog;
import stsc.frontend.zozka.gui.models.feedzilla.FeedzillaArticleDescription;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.frontend.zozka.settings.ControllerHelper;
import stsc.news.feedzilla.FeedzillaFileStorage;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;

public class FeedzillaArticlesPane extends BorderPane {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	final private BorderPane mainPane = new BorderPane();
	private Stage owner;

	@FXML
	private Label datafeedLabel;

	private ObservableList<FeedzillaArticleDescription> model = FXCollections.observableArrayList();
	@FXML
	private TableView<FeedzillaArticleDescription> newsTable;
	@FXML
	private TableColumn<FeedzillaArticleDescription, String> dateColumn;

	private final ProgressWithStopPane progressWithStopPane = new ProgressWithStopPane();

	public FeedzillaArticlesPane() throws IOException {
		final Parent gui = initializeGui();
		validateGui();
		setUpTable();
		mainPane.setCenter(gui);
		mainPane.setBottom(null);
	}

	private void setUpTable() {
		newsTable.setItems(model);
		dateColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaArticleDescription, String>("date"));
	}

	private Parent initializeGui() throws IOException {
		final URL location = FeedzillaArticlesPane.class.getResource("05_zozka_feedzilla_visualiser_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent result = loader.load();
		return result;
	}

	private void validateGui() {
		assert newsTable != null : "fx:id=\"newsTable\" was not injected: check your FXML file.";
		assert dateColumn != null : "fx:id=\"dateColumn\" was not injected: check your FXML file.";
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";
	}

	@FXML
	private void datafeedClicked(MouseEvent event) {
		if (event.getClickCount() == 2) {
			try {
				chooseFolder();
			} catch (Exception e) {
				Dialogs.create().owner(owner).showException(e);
			}
		}
	}

	private void chooseFolder() throws FileNotFoundException, IOException {
		if (ControllerHelper.chooseFolder(owner, datafeedLabel)) {
			chooseDate();
		}
	}

	private void chooseDate() throws FileNotFoundException, IOException {
		final DatePickerDialog pickDate = new DatePickerDialog("Choose Date", owner, LocalDate.of(1990, 1, 1));
		pickDate.showAndWait();
		if (pickDate.isOk()) {
			loadFeedzillaFileStorage(pickDate.getDate());
		}
	}

	private void loadFeedzillaFileStorage(LocalDate localDate) {
		progressWithStopPane.show();
		progressWithStopPane.setIndicatorProgress(0.0);
		mainPane.setBottom(progressWithStopPane);
		final String feedFolder = datafeedLabel.getText();
		Platform.runLater(() -> {
			loadFeedzillaDataFromFileStorage(feedFolder, localDate);
		});
	}

	private void loadFeedzillaDataFromFileStorage(String feedFolder, LocalDate localDate) {
		final FeedzillaFileStorage ffs = new FeedzillaFileStorage(feedFolder, createDate(localDate), true);
		ffs.addReceiver(new ReceiverToIndicatorProcess(progressWithStopPane));
		try {
			ffs.readData();
		} catch (Exception e) {
			Dialogs.create().owner(owner).showException(e);
		}
		final Map<Date, List<FeedzillaFileArticle>> data = ffs.getArticlesByDate();
		int index = 0;
		for (Entry<Date, List<FeedzillaFileArticle>> entry : data.entrySet()) {
			for (FeedzillaFileArticle article : entry.getValue()) {
				model.add(new FeedzillaArticleDescription(index, entry.getKey()));
				index += 1;
			}
		}
	}

	public void setMainWindow(Stage owner) {
		this.owner = owner;
	}

	public BorderPane getMainPane() {
		return mainPane;
	}

	private Date createDate(LocalDate date) {
		return new Date(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	private static class ReceiverToIndicatorProcess implements FeedzillaFileStorage.Receiver {

		private final ProgressWithStopPane progressWithStopPane;
		private double size = 0;
		private double index = 0;

		ReceiverToIndicatorProcess(ProgressWithStopPane progressWithStopPane) {
			this.progressWithStopPane = progressWithStopPane;
		}

		@Override
		public void allArticleFilesSize(int allArticlesFilesCount) {
			size = allArticlesFilesCount;
		}

		@Override
		public void processedArticleFile(String articleFileName) {
			index += 1.0;
			progressWithStopPane.setIndicatorProgress(index / size);
			if (Double.compare(index, size) == 0) {
				progressWithStopPane.hide();
			}
		}

		@Override
		public boolean addArticle(FeedzillaFileArticle article) {
			return true;
		}
	}
}
