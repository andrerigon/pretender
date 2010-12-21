package org.pretender.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

public class LazyDeserializer extends SkeletonDeserializer {
	final Map<String, JsonElement> children;
	final JsonDeserializationContext context;
	private final Map<String, Object> properties = new HashMap<String, Object>();

	public LazyDeserializer(JsonElement elem, JsonDeserializationContext context) {
		this.children = childrenOf(elem);
		this.context = context;
	}

	public Object property(Method method) {
		final String name = propertyName(method);
		if (!properties.containsKey(name)) {
			properties.put(name, deserialize(method.getGenericReturnType(), children.get(name), context));
			children.remove(name);
		}
		return properties.get(name);
	}
}
