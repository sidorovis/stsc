package stsc.general.simulator.multistarter;

public abstract class MpIterator<T> implements ResetableIterator<T>, Cloneable {

	private final String name;

	protected MpIterator(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final Parameter<T> currentParameter() {
		return new Parameter<T>(getName(), current());
	}

	public abstract void increment();

	public abstract long size();

	public final Parameter<T> getParameterByIndex(int index) {
		return new Parameter<T>(getName(), parameter(index));
	}

	public abstract T parameter(int index);

	public abstract MpIterator<T> clone();

	public final T getRangom() {
		return parameter((int) (Math.random() * size()));
	}

	public abstract int getIndexByValue(String value);

	public abstract int getIndexByValue(T value);

	public final T mutate(String leftValue, String rightValue) {
		final int leftIndex = getIndexByValue(leftValue);
		final int rightIndex = getIndexByValue(rightValue);
		return mutateByIndex(leftIndex, rightIndex);
	}

	protected T mutateByIndex(int leftIndex, int rightIndex) {
		int newIndex = 0;
		if (leftIndex < 0 || rightIndex < 0) {
			return parameter((int) (size() / 2));
		}
		while (true) {
			newIndex = mutateIndexes(leftIndex, rightIndex);
			if (newIndex >= leftIndex && newIndex < rightIndex)
				break;
		}
		return parameter(newIndex);
	}

	private int mutateIndexes(int leftIndex, int rightIndex) {
		int newIndex = 0;
		int binaryIndex = 0;
		while (leftIndex > 0 || rightIndex > 0) {
			final int l = (leftIndex & 1);
			final int r = (rightIndex & 1);
			final double random = Math.random();
			if (l == 1 && r == 1 && random >= 0.1) {
				newIndex |= 1 << (binaryIndex);
			} else if ((l + r) == 1 && random >= 0.5) {
				newIndex |= 1 << (binaryIndex);
			} else if ((l + r) == 0 && random >= 0.9) {
				newIndex |= 1 << (binaryIndex);
			}
			binaryIndex += 1;
			leftIndex >>= 1;
			rightIndex >>= 1;
		}
		return newIndex;
	}
}
