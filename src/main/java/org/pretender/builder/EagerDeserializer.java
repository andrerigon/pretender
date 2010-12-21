package org.pretender.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

public class EagerDeserializer extends SkeletonDeserializer {

	private final Map<String, Object> properties = new HashMap<String, Object>();

	public EagerDeserializer(JsonElement elem, JsonDeserializationContext context, Class<?> clazz) {
		init(clazz, elem, context);
	}

	private void init(Class<?> clazz, JsonElement elem, JsonDeserializationContext context) {
		for (Method m : clazz.getDeclaredMethods()) {
			properties.put(m.getName(), deserialize(m.getGenericReturnType(), childOf(elem, propertyName(m)), context));
		}
	}

	public Object property(Method method) {
		return properties.get(method.getName());
	}
}