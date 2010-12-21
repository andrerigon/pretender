package org.pretender.builder;

import java.lang.reflect.Method;

public interface Deserializer {
	Object property(Method method);
}
