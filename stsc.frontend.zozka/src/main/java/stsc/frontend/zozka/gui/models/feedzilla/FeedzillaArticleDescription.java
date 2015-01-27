package stsc.frontend.zozka.gui.models.feedzilla;

import java.util.Date;

import org.joda.time.DateTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FeedzillaArticleDescription {

	final private int index;
	final private Date publishDate;

	public FeedzillaArticleDescription(final int index, Date publishDate) {
		this.index = index;
		this.publishDate = new DateTime(publishDate).withTimeAtStartOfDay().toDate();
	}

	public StringProperty dateProperty() {
		return new SimpleStringProperty("" + index + " " + publishDate);
	}
}
