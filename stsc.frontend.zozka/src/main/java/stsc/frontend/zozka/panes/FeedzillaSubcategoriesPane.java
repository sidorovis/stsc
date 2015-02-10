package stsc.frontend.zozka.panes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import stsc.frontend.zozka.gui.models.feedzilla.FeedzillaSubcategoryDescription;
import stsc.frontend.zozka.settings.ControllerHelper;
import stsc.news.feedzilla.FeedzillaHashStorage;
import stsc.news.feedzilla.FeedzillaHashStorageReceiver;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public class FeedzillaSubcategoriesPane extends BorderPane implements FeedzillaHashStorageReceiver {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	@FXML
	private Label datafeedLabel;

	private final ObservableList<FeedzillaSubcategoryDescription> model = FXCollections.observableArrayList();

	@FXML
	private TableView<FeedzillaSubcategoryDescription> subcategoriesTable;
	@FXML
	private TableColumn<FeedzillaSubcategoryDescription, String> idColumn;
	@FXML
	private TableColumn<FeedzillaSubcategoryDescription, String> nameColumn;
	@FXML
	private TableColumn<FeedzillaSubcategoryDescription, String> urlColumn;
	@FXML
	private TableColumn<FeedzillaSubcategoryDescription, String> categoryColumn;

	private final Stage owner;

	public FeedzillaSubcategoriesPane(Stage owner) throws IOException {
		this.owner = owner;
		final Parent gui = initializeGui();
		validateGui();
		setUpTable();
		this.setCenter(gui);
	}

	private Parent initializeGui() throws IOException {
		final URL location = FeedzillaArticlesPane.class.getResource("05_zozka_feedzilla_subcategories_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent result = loader.load();
		return result;
	}

	private void validateGui() {
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";
		assert subcategoriesTable != null : "fx:id=\"subcategoriesTable\" was not injected: check your FXML file.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file.";
		assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file.";
		assert urlColumn != null : "fx:id=\"urlColumn\" was not injected: check your FXML file.";
		assert categoryColumn != null : "fx:id=\"categoryColumn\" was not injected: check your FXML file.";
	}

	private void setUpTable() {
		subcategoriesTable.setItems(model);
		idColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaSubcategoryDescription, String>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaSubcategoryDescription, String>("name"));
		urlColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaSubcategoryDescription, String>("url"));
		categoryColumn.setCellValueFactory(new PropertyValueFactory<FeedzillaSubcategoryDescription, String>("categoryName"));
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
		loadFeedzillaDataFromFileStorage(feedFolder, dateDownloadFrom);
	}

	private void loadFeedzillaDataFromFileStorage(String feedFolder, LocalDateTime dateDownloadFrom) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				downloadData(feedFolder, dateDownloadFrom);
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

	@Override
	public boolean addArticle(FeedzillaFileArticle article) {
		return true;
	}

	@Override
	public void addCategory(FeedzillaFileCategory category) {
	}

	@Override
	public void addSubCategory(FeedzillaFileSubcategory subcategory) {
		model.add(new FeedzillaSubcategoryDescription(subcategory.getId(), subcategory.getEnglishSubcategoryName(), subcategory
				.getUrlSubcategoryName(), subcategory.getCategory().getEnglishCategoryName()));
	}

}
