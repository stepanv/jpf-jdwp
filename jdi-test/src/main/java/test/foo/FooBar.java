package test.foo;

public class FooBar {

	public static void main(String[] args) {
		try {
			doit();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("hello3");
	}
	
	public static void doit() {
		try {
			System.out.println("hello1");
			hello();
			System.out.println("hello1");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static String hello() {
		throw new Error("foobar");
		//return "Hello";
	}
}
