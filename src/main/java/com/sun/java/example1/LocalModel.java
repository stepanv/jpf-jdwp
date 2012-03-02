package com.sun.java.example1;

//Place  functionality in a local object 
public class LocalModel implements java.io.Serializable {
	public String getVersionNumber() {
		System.out.println("local model");
		return "Version 1.0";
	}
}
