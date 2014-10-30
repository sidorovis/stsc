package stsc.frontend.zozka.controllers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.collections.ObservableList;

import org.junit.Test;

import stsc.frontend.zozka.gui.models.AlgorithmType;
import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.frontend.zozka.gui.models.NumberAlgorithmParameter;
import stsc.frontend.zozka.gui.models.ParameterType;
import stsc.frontend.zozka.gui.models.TextAlgorithmParameter;
import stsc.frontend.zozka.models.SimulatorSettingsModel;

public class TestSimulatorSettingsModel {

	@Test
	public void testSaveLoad() throws FileNotFoundException, IOException, ClassNotFoundException {
		final File f = new File("./test/file_name");
		{
			final SimulatorSettingsModel model = new SimulatorSettingsModel();
			final ExecutionDescription ed1 = new ExecutionDescription(AlgorithmType.STOCK_VALUE, "ename1", "in_algorithm");
			ed1.getNumberAlgorithms().add(new NumberAlgorithmParameter("firstPN", ParameterType.INTEGER, "1", "2", "5"));
			ed1.getNumberAlgorithms().add(new NumberAlgorithmParameter("secondPN", ParameterType.DOUBLE, "5.23", "3.12", "23.54"));
			ed1.getTextAlgorithms().add(
					new TextAlgorithmParameter("thirdPN", ParameterType.SUB_EXECUTION, TextAlgorithmParameter
							.createDomenRepresentation("'asd','fds','greg','tyjt'")));
			ed1.getTextAlgorithms().add(
					new TextAlgorithmParameter("fourthPN", ParameterType.STRING, TextAlgorithmParameter
							.createDomenRepresentation("'in','out','step','bbb','hello','world'")));
			model.add(ed1);

			final ExecutionDescription ed2 = new ExecutionDescription(AlgorithmType.EOD_VALUE, "eNewName1", "OneExample");
			ed2.getNumberAlgorithms().add(new NumberAlgorithmParameter("TheSecondPN", ParameterType.DOUBLE, "2.54", "4.12", "26.12"));
			ed2.getNumberAlgorithms().add(new NumberAlgorithmParameter("TheFirstPN", ParameterType.INTEGER, "6", "3", "55"));
			ed2.getTextAlgorithms().add(
					new TextAlgorithmParameter("TheFourthPN", ParameterType.STRING, TextAlgorithmParameter
							.createDomenRepresentation("'I','hate','stupid','people','who','are_not_writing_tests'")));
			ed2.getTextAlgorithms().add(
					new TextAlgorithmParameter("TheThirdPN", ParameterType.SUB_EXECUTION, TextAlgorithmParameter
							.createDomenRepresentation("'And','happinnes','always','gone'")));
			model.add(ed2);
			model.saveToFile(f);
		}
		{
			final SimulatorSettingsModel model = new SimulatorSettingsModel();
			model.loadFromFile(f);
			assertEquals(2, model.size());

			final ObservableList<NumberAlgorithmParameter> nap0 = model.getModel().get(0).getNumberAlgorithms();
			final ObservableList<TextAlgorithmParameter> tap0 = model.getModel().get(0).getTextAlgorithms();
			final ObservableList<NumberAlgorithmParameter> nap1 = model.getModel().get(1).getNumberAlgorithms();
			final ObservableList<TextAlgorithmParameter> tap1 = model.getModel().get(1).getTextAlgorithms();

			assertEquals(2, nap0.size());
			assertEquals(2, tap0.size());
			assertEquals(2, nap1.size());
			assertEquals(2, tap1.size());

			assertEquals(ParameterType.INTEGER, nap0.get(0).getType());
			assertEquals(ParameterType.DOUBLE, nap0.get(1).getType());

			assertEquals(ParameterType.INTEGER, nap1.get(1).getType());
			assertEquals(ParameterType.DOUBLE, nap1.get(0).getType());

			assertEquals(ParameterType.SUB_EXECUTION, tap0.get(0).getType());
			assertEquals(ParameterType.STRING, tap0.get(1).getType());

			assertEquals(ParameterType.SUB_EXECUTION, tap1.get(1).getType());
			assertEquals(ParameterType.STRING, tap1.get(0).getType());

			assertEquals("1", nap0.get(0).getFrom());
			assertEquals("2", nap0.get(0).getStep());
			assertEquals("5", nap0.get(0).getTo());

			assertEquals("5.23", nap0.get(1).getFrom());
			assertEquals("3.12", nap0.get(1).getStep());
			assertEquals("23.54", nap0.get(1).getTo());

			assertEquals("6", nap1.get(1).getFrom());
			assertEquals("3", nap1.get(1).getStep());
			assertEquals("55", nap1.get(1).getTo());

			assertEquals("2.54", nap1.get(0).getFrom());
			assertEquals("4.12", nap1.get(0).getStep());
			assertEquals("26.12", nap1.get(0).getTo());

			assertEquals(4, TextAlgorithmParameter.createDomenRepresentation(tap0.get(0).domenProperty().getValue()).size());
			assertEquals(6, TextAlgorithmParameter.createDomenRepresentation(tap0.get(1).domenProperty().getValue()).size());

			assertEquals(6, TextAlgorithmParameter.createDomenRepresentation(tap1.get(0).domenProperty().getValue()).size());
			assertEquals(4, TextAlgorithmParameter.createDomenRepresentation(tap1.get(1).domenProperty().getValue()).size());
		}
		assertEquals(true, f.delete());
	}
}
