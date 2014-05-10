package stsc.simulator;

import org.apache.commons.lang3.Validate;

import stsc.trading.TradeProcessorInit;

public class SimulatorSettings {

	private final TradeProcessorInit tradeProcessorInit;

	public SimulatorSettings(TradeProcessorInit tradeProcessorInit) {
		Validate.notNull(tradeProcessorInit);
		this.tradeProcessorInit = tradeProcessorInit;
	}

	public TradeProcessorInit getInit() {
		return tradeProcessorInit;
	}

	public String stringHashCode() {
		return tradeProcessorInit.stringHashCode();
	}

}
