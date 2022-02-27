package validators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import net.sf.oval.exception.OValException;
import validators.If.IfOption;

public class IfCheck {

	private IfOption option;
	private String fieldName;
	private String valueToCompare;

	public IfCheck(If ifValidation) {

		this.option = ifValidation.option();
		this.fieldName = ifValidation.attribute();
		this.valueToCompare = ifValidation.value();

	}

	public boolean isSatisfied(Object validatedObject) throws OValException {

		try {

			Boolean satisfied = false;

			String[] fieldNames = fieldName.split("\\.");

			Field field = validatedObject.getClass().getDeclaredField(fieldNames[0]);

			if(fieldNames.length > 1) {

				for (int i = 1; i < fieldNames.length; i++) {

					validatedObject = field.get(validatedObject);

					if(validatedObject == null)
						return true;

					field = validatedObject.getClass().getDeclaredField(fieldNames[i]);

				}

			}

			switch (this.option) {

				case TRUE:

					Object fieldValue = field.get(validatedObject);
					satisfied = fieldValue == null ? false : (Boolean) fieldValue;
					break;

				case FALSE:

					fieldValue = field.get(validatedObject);
					satisfied = fieldValue == null ? false : ! (Boolean) fieldValue;
					break;

				case NULL:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null)
						satisfied = true;

					break;

				case NOT_NULL:

					fieldValue = field.get(validatedObject);

					if(fieldValue != null)
						satisfied = true;

					break;

				case EMPTY:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null)
						satisfied = true;
					else if(fieldValue instanceof Collection && ((Collection) fieldValue).isEmpty())
						satisfied = true;
					else if(fieldValue instanceof String && ((String) fieldValue).isEmpty())
						satisfied = true;

					break;

				case NOT_EMPTY:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null)
						satisfied = false;
					else if(fieldValue instanceof Collection && !((Collection) fieldValue).isEmpty())
						satisfied = true;
					else if(fieldValue instanceof String && !((String) fieldValue).isEmpty())
						satisfied = true;

					break;

				case EQUALS:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null) {
						satisfied = false;
					} else {

						Object valueToCompare = this.getValueToCompare(field.getType(), this.valueToCompare);

						if(fieldValue.equals(valueToCompare))
							satisfied = true;

					}

					break;

				case NOT_EQUALS:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null) {
						satisfied = true;
					} else {

						Object valueToCompare = this.getValueToCompare(field.getType(), this.valueToCompare);

						if(!fieldValue.equals(valueToCompare))
							satisfied = true;

					}

					break;

				case GREATER_THAN:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null) {
						satisfied = true;
					} else {

						Comparable valueToCompare = (Comparable) this.getValueToCompare(field.getType(), this.valueToCompare);

						if(valueToCompare.compareTo(fieldValue) == 1)
							satisfied = true;

					}

					break;

				case LESS_TEHN:

					fieldValue = field.get(validatedObject);

					if(fieldValue == null) {
						satisfied = true;
					} else {

						Comparable valueToCompare = (Comparable) this.getValueToCompare(field.getType(), this.valueToCompare);

						if(valueToCompare.compareTo(fieldValue) == -1)
							satisfied = true;

					}

					break;

			}

			return satisfied;

		} catch (SecurityException e) {
			throw new OValException(e);
		} catch (NoSuchFieldException e) {
			throw new OValException("Especified field not found!", e);
		} catch (IllegalArgumentException e) {
			throw new OValException(e);
		} catch (IllegalAccessException e) {
			throw new OValException(e);
		}

	}

	@SuppressWarnings("deprecation")
	private Object getValueToCompare(Class type, String valueToCompare) {

		if(type.equals(String.class)) {

			return valueToCompare;

		} else if (type.equals(Date.class)) {

			String[] data = valueToCompare.split("/");

			return new Date(Integer.valueOf(data[2])-1900, Integer.valueOf(data[1])-1, Integer.valueOf(data[0]));

		} else {
			Method method;
			try {

				method = type.getDeclaredMethod("valueOf", String.class);

			} catch (SecurityException e) {
				throw new OValException("Field type '" + type.getSimpleName() + "' not supported for check", e);
			} catch (NoSuchMethodException e) {
				throw new OValException("Field type '" + type.getSimpleName() + "' not supported for check. No static method valueOf(String) found!", e);
			}

			try {
				return method.invoke(null, valueToCompare);
			} catch (IllegalArgumentException e) {
				throw new OValException("Field type '" + type.getSimpleName() + "' not supported for check. No static method valueOf(String) found!", e);
			} catch (IllegalAccessException e) {
				throw new OValException("Field type '" + type.getSimpleName() + "' not supported for check. The method valueOf(String) is not public!", e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NullPointerException e) {
				throw new OValException("Field type '" + type.getSimpleName() + "' not supported for check. No static method valueOf(String) found!", e);
			}

		}

	}

}
