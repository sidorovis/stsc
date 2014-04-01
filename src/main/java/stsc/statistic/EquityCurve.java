package stsc.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class EquityCurve implements Cloneable {

	static public class EquityCurveElement implements Cloneable {

		public Date date;
		public double value;

		public EquityCurveElement(Date date, double value) {
			super();
			this.date = date;
			this.value = value;
		}

		public static EquityCurveElement makeForSearch(Date date) {
			return new EquityCurveElement(date);
		}

		private EquityCurveElement(Date date) {
			this.date = date;
		}

		public void reverse(double divider) {
			value = -value / divider;
		}

		public String toString() {
			return "ece(" + String.format("%03f", value) + ")";
		}

		@Override
		public EquityCurveElement clone() {
			return new EquityCurveElement(this.date, this.value);
		}

	};

	static public class ElementComparator implements Comparator<EquityCurveElement> {

		@Override
		public int compare(EquityCurveElement o1, EquityCurveElement o2) {
			return o1.date.compareTo(o2.date);
		}
	}

	static final ElementComparator equityCurveElementComparator = new ElementComparator();

	private ArrayList<EquityCurveElement> elements = new ArrayList<>();

	public EquityCurve() {
	}

	@Override
	public EquityCurve clone() {
		return new EquityCurve(this);
	}

	private EquityCurve(final EquityCurve equityCurve) {
		for (EquityCurveElement e : equityCurve.elements) {
			this.elements.add(e.clone());
		}
	}

	public int size() {
		return elements.size();
	}

	public void add(Date date, double value) {
		elements.add(new EquityCurveElement(date, value));
	}

	public EquityCurveElement getLastElement() {
		return elements.get(elements.size() - 1);
	}

	public void recalculateWithMax(double maximumSpentMoney) {
		for (int i = 0; i < size(); ++i) {
			elements.get(i).reverse(maximumSpentMoney);
		}
	}

	public EquityCurveElement get(int i) {
		return elements.get(i);
	}

	public int find(Date date) {
		int index = Collections.binarySearch(elements, EquityCurveElement.makeForSearch(date),
				equityCurveElementComparator);
		if (index < 0)
			index = -index - 1;
		return index;
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	public void setLast(final double value) {
		elements.get(elements.size() - 1).value = value;
	}
}
