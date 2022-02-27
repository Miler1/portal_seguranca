package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtil {

	public static <T extends Identificavel> List<Integer> getIds(Collection<T> models) {

		if (models == null || models.isEmpty()) {

			return new ArrayList<>();

		}

		List<Integer> ids = new ArrayList<>();

		for (Identificavel model : models) {

			ids.add(model.getId() != null ? model.getId() : 0);

		}

		return ids;

	}

	public static <T extends Object> String toString(Collection<T> objs, String separator) {

		return toString(objs, separator, null, null);

	}

	public static <T extends Object> String toString(Collection<T> objs, String separator, String elementPrefix, String elementPosfix) {

		elementPosfix = (elementPosfix != null) ? elementPosfix : "";
		elementPrefix = (elementPrefix != null) ? elementPrefix : "";
		String str = "";

		if (objs != null && objs.size() > 0) {

			for (Object obj : objs) {

				if (!str.isEmpty()) {

					str += separator;

				}

				str += elementPrefix + obj + elementPosfix;

			}

		}

		return str;

	}

}