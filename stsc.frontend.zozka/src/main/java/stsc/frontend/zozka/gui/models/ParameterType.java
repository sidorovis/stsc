package stsc.frontend.zozka.gui.models;

import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;

public enum ParameterType {

	INTEGER("Integer", Integer.class, MpInteger.class), DOUBLE("Double", Double.class, MpDouble.class), STRING("String", String.class,
			MpString.class), SUB_EXECUTION("Sub Execution", String.class, MpSubExecution.class);

	private final String name;
	private final Class<?> classType;
	private final Class<? extends MpIterator<?>> iteratorType;

	private <T> ParameterType(String name, Class<T> classType, Class<? extends MpIterator<T>> iteratorType) {
		this.name = name;
		this.classType = classType;
		this.iteratorType = iteratorType;
	}

	public String getName() {
		return name;
	}

	public Class<?> getClassType() {
		return classType;
	}

	public Class<? extends MpIterator<?>> getIteratorType() {
		return iteratorType;
	}

	public boolean isInteger() {
		return this.equals(INTEGER);
	}

	public boolean isDouble() {
		return this.equals(DOUBLE);
	}

	public boolean isString() {
		return this.equals(STRING);
	}

	public boolean isSubString() {
		return this.equals(SUB_EXECUTION);
	}
}
