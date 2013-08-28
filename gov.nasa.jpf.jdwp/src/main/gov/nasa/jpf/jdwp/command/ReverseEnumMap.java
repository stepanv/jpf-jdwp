package gov.nasa.jpf.jdwp.command;

import gov.nasa.jpf.jdwp.exception.JdwpError;
import gov.nasa.jpf.jdwp.exception.JdwpError.ErrorType;

import java.util.HashMap;
import java.util.Map;

public class ReverseEnumMap<T, V extends Enum<V> & IdentifiableEnum<T>> {
  private Map<T, V> map = new HashMap<T, V>();

  public ReverseEnumMap(Class<V> enumIdentifierClassType) {
    for (V v : enumIdentifierClassType.getEnumConstants()) {
      if (v.identifier() != null) {
        map.put(v.identifier(), v);
      }
    }
  }

  public V get(T num) throws JdwpError {
    V value = map.get(num);
    if (value != null) {
      return value;
    }
    throw new JdwpError(ErrorType.NOT_IMPLEMENTED, "Mapping doesn't exist for: " + num);
  }

}
