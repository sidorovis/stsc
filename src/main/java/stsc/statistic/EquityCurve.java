package stsc.statistic;

import java.util.ArrayList;
import java.util.Date;

public class EquityCurve {

	public class EquityCurveElement {
		public EquityCurveElement(Date date, double value) {
			super();
			this.date = date;
			this.value = value;
		}

		public Date date;
		public double value;
		
		public void reverse(double divider){
			value = -value / divider;
		}
	};

	private ArrayList<EquityCurveElement> elements = new ArrayList<>();

	public int size() {
		return elements.size();
	}

	public void add(Date date, double value) {
		elements.add(new EquityCurveElement(date, value));
	}

	public double getLastValue() {
		return elements.get(elements.size() - 1).value;
	}

	public void recalculateWithMax(double maximumSpentMoney) {
		for (int i = 0; i < size(); ++i) {
			elements.get(i).reverse( maximumSpentMoney ); 
		}
	}

	public EquityCurveElement get(int i) {
		return elements.get(i);
	}
}
