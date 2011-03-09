package org.pretender;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import org.pretender.builder.JsonObjectHandler;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class ProxyJSONDeserializer<T> implements JsonDeserializer<T> {

	private final boolean lazy;

	ProxyJSONDeserializer(boolean lazy) {
		this.lazy = lazy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonElement elem, Type type, JsonDeserializationContext context) throws JsonParseException {
		if (lazy) {
			return JsonObjectHandler.newLazyBean(elem, context, typeFor(type));
		}
		return JsonObjectHandler.newEagerBean(elem, context, (Class<T>) type);
	}

    private Class<T> typeFor(Type type) {
        if( type instanceof WildcardType){
             return (Class<T>)((WildcardType)type).getUpperBounds()[0];
        }
        return (Class<T>) type;
    }

}