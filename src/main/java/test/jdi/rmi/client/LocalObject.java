package test.jdi.rmi.client;

import java.io.Serializable;

public class LocalObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5633935413105018549L;

	public String sayHello() {
		System.out.println("where am I?");
		return "hello";
	}
}
