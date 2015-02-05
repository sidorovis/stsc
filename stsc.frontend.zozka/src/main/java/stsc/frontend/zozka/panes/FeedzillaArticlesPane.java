package stsc.frontend.zozka.panes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.dialogs.DatePickerDialog;
import stsc.frontend.zozka.gui.models.feedzilla.FeedzillaArticleDescription;
import stsc.frontend.zozka.settings.ControllerHelper;
import stsc.news.feedzilla.FeedzillaFileStorage.Receiver;
import stsc.news.feedzilla.FeedzillaHashStorage;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;

public class FeedzillaArticlesPane extends BorderPane implements Receiver {

	private final static int ARTICLES_PER_PAGE = 100;

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	final private BorderPane mainPane = new BorderPane();
	private Stage owner;

	@FXML
	private Label datafeedLabel;

	private List<ObservableList<FeedzillaArticleDescription>> pagedModels = new ArrayList<>();

	@FXML
	private TableView<FeedzillaArticleDescription> newsTable;
	@FXML
	private TableColumn<FeedzillaArticleDescription, String> authorColumn;
	@FXML
	private TableColumn<FeedzillaArticleDescription, String> titleColumn;
	@FXML
	private TableColumn<FeedzillaArticleDescription, String> dateColumn;

	@FXML
	private Pagination pagination;

	public FeedzillaArticlesPane() throws IOException {
		final Parent gui = initializeGui();
		validateGui();
		setUpTable();
		setUpPaginator();
		mainPane.setCenter(gui);
		newsTable.setVisible(false);
		// mainPane.setBottom(null);
	}

	private void setUpTable() {
		pagedModels.add(FXCollections.observableArrayList());
		newsTable.setItems(pagedModels.get(0));
		authorColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaArticleDescription, String>("author"));
		titleColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaArticleDescription, String>("title"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaArticleDescription, String>("date"));
	}

	private void setUpPaginator() {
		pagination.setPageCount(1);

		pagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer param) {
				if (param > 0) {
					newsTable.setItems(pagedModels.get(param - 1));
				}
				return new BorderPane(newsTable);
			}
		});
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
		assert pagination != null : "fx:id=\"pagination\" was not injected: check your FXML file.";
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
			loadFeedzillaFileStorage(pickDate.getDate().atStartOfDay());
		}
	}

	private void loadFeedzillaFileStorage(LocalDateTime dateDownloadFrom) {
		final String feedFolder = datafeedLabel.getText();
		Platform.runLater(() -> {
			loadFeedzillaDataFromFileStorage(feedFolder, dateDownloadFrom);
		});
	}

	private void loadFeedzillaDataFromFileStorage(String feedFolder, LocalDateTime dateDownloadFrom) {
		pagination.setPageCount(1);
		pagedModels.clear();
		pagedModels.add(FXCollections.observableArrayList());
		newsTable.setItems(pagedModels.get(0));
		new Thread(new Runnable() {
			@Override
			public void run() {
				downloadData(feedFolder, dateDownloadFrom);
				Platform.runLater(() -> {
					newsTable.setVisible(true);
				});
			}
		}).start();
	}

	private void downloadData(String feedFolder, LocalDateTime dateDownloadFrom) {
		try {
			final FeedzillaHashStorage hashStorage = new FeedzillaHashStorage(feedFolder);
			hashStorage.addReceiver(this);
			hashStorage.readFeedDataAndStore(dateDownloadFrom);
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.create().owner(owner).showException(e);
			});
		}
	}

	public void setMainWindow(Stage owner) {
		this.owner = owner;
	}

	public BorderPane getMainPane() {
		return mainPane;
	}

	@Override
	public void allArticleFilesSize(int allArticlesFilesCount) {
	}

	@Override
	public void processedArticleFile(String articleFileName) {
	}

	@Override
	public boolean addArticle(FeedzillaFileArticle article) {
		final int modelIndex = pagedModels.size() - 1;
		ObservableList<FeedzillaArticleDescription> currentPagedModel = pagedModels.get(modelIndex);
		if (currentPagedModel.size() == ARTICLES_PER_PAGE) {
			currentPagedModel = FXCollections.observableArrayList();
			pagedModels.add(currentPagedModel);
			Platform.runLater(() -> pagination.setPageCount(pagedModels.size()));
		}
		currentPagedModel.add(new FeedzillaArticleDescription(article.getAuthor(), article.getTitle(), article.getPublishDate()));
		return false;
	}

}
