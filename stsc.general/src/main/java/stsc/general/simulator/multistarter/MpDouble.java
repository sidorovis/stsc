package stsc.general.simulator.multistarter;

import stsc.common.Settings;

import com.google.common.math.DoubleMath;

public class MpDouble extends MpIterator<Double> {

	private final double from;
	private final double to;
	private final double step;
	private int iterator;

	public MpDouble(String name, double from, double to, double step) throws BadParameterException {
		super(name);
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
		return new MpDouble(getName(), from, to, step, true);
	}

	private MpDouble(String name, double from, double to, double step, boolean privateBoolean) {
		super(name);
		this.from = from;
		this.to = to;
		this.step = step;
		this.iterator = 0;
	}

	@Override
	public long size() {
		long result = Math.round((to - from) / step);
		return (result == 0) ? 1 : result;
	}

	@Override
	public String toString() {
		return getName() + ":" + String.valueOf(current()) + " from (" + String.valueOf(step) + "|" + String.valueOf(from) + ":" + String.valueOf(to) + ")";
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

	@Override
	public int getIndexByValue(String value) {
		final double v = Double.valueOf(value);
		return getIndexByValue(v);
	}

	@Override
	public int getIndexByValue(Double value) {
		final int index = (int) Math.round((value - from) / step);
		return index;
	}

	public final Double mutate(Double leftValue, Double rightValue) {
		final int leftIndex = getIndexByValue(leftValue);
		final int rightIndex = getIndexByValue(rightValue);
		return mutateByIndex(leftIndex, rightIndex);
	}

}
