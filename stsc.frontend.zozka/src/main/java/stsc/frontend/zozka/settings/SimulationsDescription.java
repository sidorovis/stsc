package stsc.frontend.zozka.settings;

import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.common.FromToPeriod;

public class SimulationsDescription {
	private String datafeedPath;
	private FromToPeriod period;

	private ObservableList<ExecutionDescription> executionDescriptions = FXCollections.observableArrayList();

	public ObservableList<ExecutionDescription> getExecutionDescriptions() {
		return executionDescriptions;
	}

	public String getDatafeedPath() {
		return datafeedPath;
	}

	public void setDatafeedPath(String datafeed) {
		datafeedPath = datafeed;
	}

	public void setPeriod(Date from, Date to) {
		period = new FromToPeriod(from, to);

	}
}
