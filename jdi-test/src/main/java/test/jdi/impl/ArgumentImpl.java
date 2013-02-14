package test.jdi.impl;

import com.sun.jdi.connect.Connector.Argument;

public class ArgumentImpl implements Argument {

	public ArgumentImpl(String string) {
		value = string;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String label() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String value;

	@Override
	public String value() {
		return value;
	}

	@Override
	public void setValue(String paramString) {
		value = paramString;
	}

	@Override
	public boolean isValid(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mustSpecify() {
		// TODO Auto-generated method stub
		return false;
	}

}
