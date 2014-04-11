package stsc.simulator.multistarter;

public class MpDouble implements MpIterator<Double> {

	private final String name;
	private final Double from;
	private final Double to;
	private final Double step;
	private Double iterator;

	public MpDouble(String name, Double from, Double to, Double step) throws BadParameterException {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
		if (from >= to)
			throw new BadParameterException("Double from should be smaller than to for " + name);
		this.step = step;
		iterator = from;
	}

	@Override
	public String toString() {
		return name + "(" + from.toString() + "->" + to.toString() + ":" + step.toString() + ")";
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
	public Parameter<Double> current() {
		final Parameter<Double> result = new Parameter<Double>(name, iterator);
		return result;
	}

	@Override
	public void increment() {
		iterator += step;
	}
}
