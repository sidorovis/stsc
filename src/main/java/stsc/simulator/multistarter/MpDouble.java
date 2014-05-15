package stsc.simulator.multistarter;

import stsc.common.Settings;

import com.google.common.math.DoubleMath;

public class MpDouble implements MpIterator<Double> {

	private final String name;
	private final double from;
	private final double to;
	private final double step;
	private int iterator;

	public MpDouble(String name, double from, double to, double step) throws BadParameterException {
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
	public MpIterator<Double> clone() {
		return new MpDouble(name, from, to, step, true);
	}

	private MpDouble(String name, double from, double to, double step, boolean privateBoolean) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.step = Double.valueOf(step);
		this.iterator = 0;
	}

	@Override
	public long size() {
		return Math.round((to - from) / step);
	}

	@Override
	public String toString() {
		return name + ":" + String.valueOf(current()) + " from (" + String.valueOf(step) + "|" + String.valueOf(from)
				+ ":" + String.valueOf(to) + ")";
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
		return Double.valueOf(from + step * iterator);
	}

	@Override
	public void increment() {
		iterator += 1;
	}

	@Override
	public Double next() {
		final Double result = current();
		increment();
		return result;
	}

	@Override
	public void remove() {
	}

	@Override
	public Double parameter(int index) {
		return Double.valueOf(from + step * index);
	}

}
