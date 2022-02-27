package validators;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import play.data.validation.Validation;
import validators.RequiredIf.Operator;

public class ValidIfCheck extends AbstractAnnotationCheck<ValidIf> {

	private If[] ifs;
	private Operator operator;

	@Override
	public void configure(ValidIf requiredIfs) {

		this.ifs = requiredIfs.ifs();
		this.operator = requiredIfs.operator();

	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {

		Boolean satisfiedIfs = true;

		if(operator.equals(Operator.OR)) {

			satisfiedIfs = false;

		}

		for(If requiredIf: ifs) {

			IfCheck ifCheck = new IfCheck(requiredIf);
			Boolean valid = ifCheck.isSatisfied(validatedObject);
			Boolean satisfiedIf = (valid && value != null);

			if(operator.equals(Operator.OR)) {

				satisfiedIfs = satisfiedIfs || satisfiedIf;

			} else {

				satisfiedIfs = satisfiedIfs && satisfiedIf;

			}

			if(!satisfiedIfs) {

				return true;

			}

		}

		Validation.current().valid(value);

		return true;

	}

}
