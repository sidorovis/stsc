package stsc.simulator.multistarter;

public class MpInteger implements MpIterator<Integer> {

	private final String name;
	private final Integer from;
	private final Integer to;
	private final Integer step;
	private Integer iterator;

	public MpInteger(String name, Integer from, Integer to, Integer step) {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
		this.step = step;
		iterator = from;
	}

	@Override
	public boolean hasNext() {
		return iterator < to;
	}

	@Override
	public void remove() {
	}

	@Override
	public void reset() {
		iterator = from;
	}

	@Override
	public Parameter<Integer> next() {
		final Parameter<Integer> result = new Parameter<Integer>(name, iterator);
		iterator = iterator + step;
		return result;
	}

}
