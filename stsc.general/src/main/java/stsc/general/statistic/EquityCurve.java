package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class EquityCurve implements Cloneable {

	static public class Element implements Cloneable {

		public Date date;
		public double value;

		public Element(Date date, double value) {
			super();
			this.date = date;
			this.value = value;
		}

		public static Element makeForSearch(Date date) {
			return new Element(date);
		}

		private Element(Date date) {
			this.date = date;
		}

		public void recalculatePercent(double divider) {
			value = value / divider;
		}

		public String toString() {
			return "ece(" + String.format("%03f", value) + ")";
		}

		@Override
		public Element clone() {
			return new Element(this.date, this.value);
		}
	};

	static public class ElementComparator implements Comparator<Element> {

		@Override
		public int compare(Element o1, Element o2) {
			return o1.date.compareTo(o2.date);
		}
	}

	static final ElementComparator equityCurveElementComparator = new ElementComparator();

	private ArrayList<Element> elements = new ArrayList<>();

	public EquityCurve() {
	}

	@Override
	public EquityCurve clone() {
		final EquityCurve equityCurve = new EquityCurve();
		equityCurve.setCopy(this);
		return equityCurve;
	}

	private void setCopy(EquityCurve copyFrom) {
		for (Element e : copyFrom.elements) {
			this.elements.add(e.clone());
		}
	}

	public int size() {
		return elements.size();
	}

	public void add(Date date, double value) {
		elements.add(new Element(date, value));
	}

	public Element getLastElement() {
		return elements.get(elements.size() - 1);
	}

	public void recalculateWithMax(double maximumSpentMoney) {
		for (int i = 0; i < size(); ++i) {
			elements.get(i).recalculatePercent(maximumSpentMoney);
		}
	}

	public Element get(int i) {
		return elements.get(i);
	}

	public int find(Date date) {
		int index = Collections.binarySearch(elements, Element.makeForSearch(date),
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
