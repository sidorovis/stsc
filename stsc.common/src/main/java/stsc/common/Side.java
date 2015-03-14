package stsc.common;

public enum Side {
	LONG, SHORT;

	public int value() {
		switch (this) {
		case LONG:
			return 1;
		case SHORT:
			return -1;
		default:
			break;
		}
		return 0;
	}

	public Side reverse() {
		if (this == LONG)
			return Side.SHORT;
		else
			return Side.LONG;
	}

	public boolean isLong() {
		return this.equals(LONG);
	}

	public boolean isShort() {
		return this.equals(SHORT);
	}
}
