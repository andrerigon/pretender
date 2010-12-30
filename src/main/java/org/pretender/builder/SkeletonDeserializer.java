package org.pretender.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pretender.annotation.BindToName;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class SkeletonDeserializer implements Deserializer {

	Object deserialize(Type type, JsonElement element, JsonDeserializationContext context) {
		return element == null ? null : context.deserialize(element, type);
	}

	JsonElement childOf(JsonElement element, String name) {
		return element.getAsJsonObject().get(name);
	}

	Map<String, JsonElement> childrenOf(JsonElement element) {
		final JsonObject parent = element.getAsJsonObject();
		final Map<String, JsonElement> children = new HashMap<String, JsonElement>();
		for (Entry<String, JsonElement> e : parent.entrySet()) {
			children.put(e.getKey(), e.getValue());
		}
		return children;
	}

	String propertyName(Method m) {
		if (m.isAnnotationPresent(BindToName.class)) {
			return m.getAnnotation(BindToName.class).value();
		}
		return removePrefix(m.getName(), MethodPrefix.values());
	}

	private static String removePrefix(String name, MethodPrefix... prefixes) {
		for (MethodPrefix prefix : prefixes) {
			String prefixName = prefix.toString().toLowerCase();
			if (!name.startsWith(prefixName)) {
				continue;
			}
			name = name.replaceAll("^" + prefixName, "");
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		return name;
	}

	static enum MethodPrefix {
		IS, GET, HAS;
	}
}
