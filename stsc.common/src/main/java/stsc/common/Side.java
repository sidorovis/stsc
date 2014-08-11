package stsc.common;

public enum Side {
	LONG, SHORT;

	public Side reverse() {
		if (this == LONG)
			return Side.SHORT;
		else
			return Side.LONG;
	}
}
