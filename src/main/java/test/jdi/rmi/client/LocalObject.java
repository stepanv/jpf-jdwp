package test.jdi.rmi.client;

import java.io.Serializable;

interface NotKnownInterface {
	public String getData();
	public void setData(String data);
}

public class LocalObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5633935413105018549L;
	private NotKnownInterface ns;

	public LocalObject(NotKnownInterface smth) {
		ns = smth;
	}
	
	
	public String sayHello() {
		System.out.println("where am I?" + ns.getData());
		return "hello";
	}
	
	public static class Nonserializable implements NotKnownInterface, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7509767607015676566L;
		private String data;
		
		public Nonserializable(String smth) {
			data = smth;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}
}
