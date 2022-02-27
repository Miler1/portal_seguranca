package validators;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import net.spy.memcached.compat.log.Log4JLogger;
import play.Logger;
import play.db.jpa.GenericModel;

public class ReferenceCheck extends AbstractAnnotationCheck<Reference> {

	/** Error message key. */
	public final static String message = "validation.reference";

	private Class modelClass;

	@Override
	public void configure(Reference reference) {

		setMessage(reference.message());
		this.modelClass = reference.modelClass();

	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {

		if(value == null || !(value instanceof GenericModel) || ((GenericModel)value)._key() == null) {

			return true;

		}

		Object id = ((GenericModel)value)._key();

		if(id == null) {

			return false;

		}

		Object reference = null;

		try {

			Method method = modelClass.getMethod("findById", Object.class);
			reference = method.invoke(null, id);

		} catch (Exception e) {

			Logger.error(e, "Erro ao validar referencia");

			return false;

		}

		if(reference == null) {

			return false;

		}

		Field field = ((FieldContext) context).getField();

		try {

			field.set(validatedObject, reference);

		} catch (IllegalArgumentException e) {

			Logger.error(e, "Erro ao preencher referencia");

			return false;

		} catch (IllegalAccessException e) {

			Logger.error(e, "Erro ao preencher referencia");

			return false;

		}

		return true;

	}

}
