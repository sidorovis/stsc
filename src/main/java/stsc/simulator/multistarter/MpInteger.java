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
		this.iterator = 0;
	}

	@Override
	public long size() {
		return Math.round((to - from) / step);
	}

	@Override
	public String toString() {
		return name + ":" + String.valueOf(current()) + " from (" + step.toString() + "|" + from.toString() + ":"
				+ to.toString() + ")";
	}

	@Override
	public boolean hasNext() {
		return current() < to;
	}

	@Override
	public void reset() {
		iterator = 0;
	}

	@Override
	public Parameter<Integer> currentParameter() {
		return new Parameter<Integer>(name, current());
	}

	@Override
	public void increment() {
		iterator += 1;
	}

	@Override
	public Integer current() {
		return from + iterator * step;
	}

	@Override
	public Integer next() {
		final Integer result = current();
		increment();
		return result;
	}

	@Override
	public void remove() {
		iterator = 0;
	}

	@Override
	public Integer parameter(int index) {
		return from + step * index;
	}
}
