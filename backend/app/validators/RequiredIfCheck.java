package validators;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import validators.RequiredIf.Operator;

public class RequiredIfCheck extends AbstractAnnotationCheck<RequiredIf> {

	/** Error message key. */
	public final static String message = "validation.required";

	private If[] ifs;
	private Operator operator;

	@Override
	public void configure(RequiredIf requiredIfs) {

		setMessage(requiredIfs.message());

		this.ifs = requiredIfs.ifs();
		this.operator = requiredIfs.operator();

	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {

		Boolean required = true;

		if(operator.equals(Operator.OR)) {

			required = false;

		}

		for(If requiredIf: ifs) {

			IfCheck ifCheck = new IfCheck(requiredIf);

			Boolean satisfiedIf = ifCheck.isSatisfied(validatedObject);

			if(operator.equals(Operator.OR)) {

				required = required || satisfiedIf;

			} else {

				required = required && satisfiedIf;

				if(!required) {

					break;

				}

			}

		}

		return (!required || value != null);

	}

}
