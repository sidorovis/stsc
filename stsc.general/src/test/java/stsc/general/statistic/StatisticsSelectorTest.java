package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.testhelper.TestStatisticsHelper;
import junit.framework.TestCase;

public class StatisticsSelectorTest extends TestCase {
	public void testStatisticsSelector() {
		final CostWeightedSumFunction compareMethod = new CostWeightedSumFunction();
		final StrategySelector statisticsSelector = new StatisticsByCostSelector(2, compareMethod);

		final List<Double> values = new ArrayList<>();
		values.add(compareMethod.calculate(TestStatisticsHelper.getStatistics(100, 200)));
		values.add(compareMethod.calculate(TestStatisticsHelper.getStatistics(200, 250)));
		values.add(compareMethod.calculate(TestStatisticsHelper.getStatistics(150, 210)));

		Iterator<SimulatorSettings> testSettings = TestGridSimulatorSettings.getGridList().iterator();

		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestStatisticsHelper.getStatistics(100, 200)));
		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestStatisticsHelper.getStatistics(200, 250)));
		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestStatisticsHelper.getStatistics(150, 210)));

		final List<TradingStrategy> strategies = statisticsSelector.getStrategies();
		assertEquals(2, strategies.size());
		assertEquals(compareMethod.calculate(((TradingStrategy) strategies.toArray()[0]).getStatistics()), values.get(2));
		assertEquals(compareMethod.calculate(((TradingStrategy) strategies.toArray()[1]).getStatistics()), values.get(0));
	}
}
