package deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.apache.commons.lang.StringUtils;
import play.Play;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {

	private static final String DATE_FORMAT = Play.configuration.getProperty("date.format");
	private static final String DATE_FORMAT_TIMETABLE = Play.configuration.getProperty("date.format.timetable");

	@Override
	public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

		String date = json.getAsJsonPrimitive().getAsString();

		if(StringUtils.isNumeric(date)|| date.substring(0,1).equals("-")) {

			return new Date(Long.valueOf(date));

		}

		if(date.matches("(\\d{4})-(\\d{2})-(\\d{2})T((\\d{2}):(\\d{2}):(\\d{2}))\\.(\\d{3})Z")) {

			return parseIsoDate(date);

		}

		if(date.matches("\\w{3}\\s(\\d{1}|\\d{2})\\,\\s\\d{4}\\s(\\d{1}|\\d{2}):\\d{2}:\\d{2}\\s\\w{2}")) {

			return parseStrangeDate(date);

		} else if (date.length() <= 10) {

			return parseDate(date);

		} else {

			return parseDateWithTimetable(date);

		}

	}

	public static Date parseIsoDate(String dateText) {

		try {

			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateText);

		} catch (ParseException e) {

			e.printStackTrace();

			return null;

		}

	}

	public static Date parseDate(String dateText) {

		try {

			return new SimpleDateFormat(DATE_FORMAT).parse(dateText);

		} catch (ParseException e) {

			e.printStackTrace();

			return null;

		}

	}

	public static Date parseDateWithTimetable(String dateText) {

		try {

			return new SimpleDateFormat(DATE_FORMAT_TIMETABLE).parse(dateText);

		} catch (ParseException e) {

			e.printStackTrace();

			return null;

		}

	}

	public static Date parseStrangeDate(String dateText) {

		try {

			return new SimpleDateFormat("MMM dd, yyyy h:mm:ss aa").parse(dateText);

		} catch (ParseException e) {

			e.printStackTrace();

			return null;

		}

	}

}
