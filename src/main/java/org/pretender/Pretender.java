package org.pretender;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

public final class Pretender {

	public static Gson gsonLazyDeserializerFor(Class<?>... classes) {
		return gsonWithAdaptersFor(true, classes);
	}

	public static Gson gsonEagerDeserializerFor(Class<?>... classes) {
		return gsonWithAdaptersFor(false, classes);
	}

	private static Gson gsonWithAdaptersFor(boolean lazy, Class<?>... classes) {
		final GsonBuilder builder = new GsonBuilder();
		@SuppressWarnings("rawtypes")
		JsonDeserializer<?> serializer = new ProxyJSONDeserializer(lazy);
		for (Class<?> clazz : classes) {
			builder.registerTypeAdapter(clazz, serializer);
		}
		return builder.create();
	}
}
