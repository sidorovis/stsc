package stsc.simulator.multistarter;

public class MPDouble implements MPIterator<Double> {
	private final Double from;
	private final Double to;
	private final Double step;
	private Double iterator;

	public MPDouble(Double from, Double to, Double step) {
		super();
		this.from = from;
		this.to = to;
		this.step = step;
		iterator = from;
	}

	@Override
	public void reset() {
		iterator = from;
	}

	@Override
	public boolean hasNext() {
		return iterator < to;
	}

	@Override
	public Double next() {
		final Double result = iterator;
		iterator = iterator + step;
		return result;
	}

	@Override
	public void remove() {
	}

}
