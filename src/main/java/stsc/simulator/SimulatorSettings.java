package stsc.simulator;

import stsc.trading.TradeProcessorInit;

public class SimulatorSettings {

	private final TradeProcessorInit tradeProcessorInit;

	public SimulatorSettings(TradeProcessorInit tradeProcessorInit) {
		this.tradeProcessorInit = tradeProcessorInit;
	}

	public TradeProcessorInit getInit() {
		return tradeProcessorInit;
	}

	@Override
	public int hashCode() {
		return tradeProcessorInit.hashCode();
	}

}
