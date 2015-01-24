package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import stsc.frontend.zozka.gui.models.feedzilla.FeedzillaArticleDescription;

public class FeedzillaArticlesPane extends BorderPane {

	final private Parent mainPane;

	@FXML
	private TableView<FeedzillaArticleDescription> newsTable;

	public FeedzillaArticlesPane() throws IOException {
		this.mainPane = initializeGui();
		validateGui();
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
	}

	@FXML
	private void datafeedClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			System.out.println(e);
		}
	}

	public Parent getMainPane() {
		return mainPane;
	}
}
