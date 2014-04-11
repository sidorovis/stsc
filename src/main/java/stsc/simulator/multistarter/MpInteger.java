package stsc.simulator.multistarter;

public class MpInteger implements MpIterator<Integer> {

	private final String name;
	private final Integer from;
	private final Integer to;
	private final Integer step;
	private Integer iterator;

	public MpInteger(String name, Integer from, Integer to, Integer step) throws BadParameterException {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
		if (from >= to)
			throw new BadParameterException("Integer from should be smaller than to for " + name);
		this.step = step;
		iterator = from;
	}

	@Override
	public String toString() {
		return name + "(" + from.toString() + "->" + to.toString() + ":" + step.toString() + ")";
	}

	@Override
	public boolean hasNext() {
		return iterator < to;
	}

	@Override
	public void reset() {
		iterator = from;
	}

	@Override
	public Parameter<Integer> current() {
		return new Parameter<Integer>(name, iterator);
	}

	@Override
	public void increment() {
		iterator += step;
	}
}
