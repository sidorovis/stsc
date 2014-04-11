package stsc.simulator.multistarter;

public class MPInteger implements MPIterator<Integer> {
	private final Integer from;
	private final Integer to;
	private final Integer step;
	private Integer iterator;

	public MPInteger(Integer from, Integer to, Integer step) {
		super();
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
	public Integer next() {
		final Integer result = iterator;
		iterator = iterator + step;
		return result;
	}

	@Override
	public void remove() {
	}

	@Override
	public void reset() {
		iterator = from;
	}

}
