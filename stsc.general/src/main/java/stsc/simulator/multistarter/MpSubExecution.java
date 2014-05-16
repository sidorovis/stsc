package stsc.simulator.multistarter;

import java.util.List;

public class MpSubExecution extends MpString {

	public MpSubExecution(final String name, final List<String> domen) throws BadParameterException {
		super(name, domen);
	}

	public MpSubExecution(final String name, final String element) throws BadParameterException {
		super(name, element);
	}

}
