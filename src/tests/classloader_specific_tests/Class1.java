package classloader_specific_tests;

public class Class1 extends Class2 implements Interface1 {
  
  Class3 field;

  public Class3 getField() {
    return field;
  }

  public void setField(Class3 field) {
    this.field = field;
  }
}
