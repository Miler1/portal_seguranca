package deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BooleanDeserializer implements JsonDeserializer<Boolean> {

	@Override
	public Boolean deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

		String string = json.getAsJsonPrimitive().getAsString();

		if((!string.toUpperCase().equals("TRUE")) && (!string.toUpperCase().equals("FALSE"))) {

			return null;

		}

		return Boolean.valueOf(string);

	}

}