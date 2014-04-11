package stsc.simulator.multistarter;

public class MpDouble implements MpIterator<Double> {

	private final String name;
	private final Double from;
	private final Double to;
	private final Double step;
	private Double iterator;

	public MpDouble(String name, Double from, Double to, Double step) {
		super();
		this.name = name;
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
	public Parameter<Double> next() {
		final Parameter<Double> result = new Parameter<Double>(name, iterator);
		iterator = iterator + step;
		return result;
	}

	@Override
	public void remove() {
	}

}
