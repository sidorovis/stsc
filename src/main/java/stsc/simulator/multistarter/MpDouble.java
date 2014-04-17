package stsc.simulator.multistarter;

import stsc.common.Settings;

import com.google.common.math.DoubleMath;

public class MpDouble implements MpIterator<Double> {

	private final String name;
	private final Double from;
	private final Double to;
	private final Double step;
	private Integer iterator;

	public MpDouble(String name, Double from, Double to, Double step) throws BadParameterException {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
		if (step == 0)
			throw new BadParameterException("Step can't be zero");
		if (from >= to)
			throw new BadParameterException("Double 'from' should be smaller than 'to' for " + name);
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
	public void reset() {
		iterator = 0;
	}

	@Override
	public boolean hasNext() {
		return DoubleMath.fuzzyCompare(current(), to, Settings.doubleEpsilon) < 0;
	}

	@Override
	public Parameter<Double> currentParameter() {
		final Parameter<Double> result = new Parameter<Double>(name, current());
		return result;
	}

	@Override
	public Double current() {
		return from + step * iterator;
	}

	@Override
	public void increment() {
		iterator += 1;
	}

	@Override
	public Double next() {
		Double result = current();
		increment();
		return result;
	}

	@Override
	public void remove() {
	}
}
