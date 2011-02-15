package org.pretender.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

public class JsonObjectHandler implements InvocationHandler {

	private final Deserializer deserializer;

	public JsonObjectHandler(Deserializer builder) {
		this.deserializer = builder;
	}

	@SuppressWarnings("unchecked")
	private static <T> T forClass(Class<T> clazz, Deserializer builder) {
		return (T) Proxy
				.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new JsonObjectHandler(builder));
	}

	public static <T> T newEagerBean(JsonElement elem, JsonDeserializationContext context, Class<T> clazz) {
		return forClass(clazz, new EagerDeserializer(elem, context, clazz));
	}

	public static <T> T newLazyBean(JsonElement elem, JsonDeserializationContext context, Class<T> clazz) {
		return forClass(clazz, new LazyDeserializer(elem, context));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if( "hashCode".equals(method.getName()) ){
            return 1;
        }
		return deserializer.property(method);
	}
}
