package validators;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

public class UniqueIfNotRemovedCheck extends AbstractAnnotationCheck<UniqueIfNotRemoved> {

	/** Error message key. */
	public final static String message = "validation.unique";

	@Override
	public void configure(UniqueIfNotRemoved uniqueIfsNotRemoved) {

		setMessage(uniqueIfsNotRemoved.message());

	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {

		if(validatedObject == null) {

			return false;

		}

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(validatedObject.getClass());
		String attribute = context.toString().substring(context.toString().lastIndexOf(".") + 1, context.toString().length());
		Integer entityId = (Integer) ((GenericModel) validatedObject).getEntityId();

		if(entityId != null) {

			crit.add(Restrictions.ne("id", entityId));

		}

		crit.add(Restrictions.eq(attribute, value));
		crit.add(Restrictions.eq("removido", false));

		if(crit.list().size() > 0) {

			return false;

		}

		return true;

	}

}
