package utils;

import exceptions.ValidationException;
import play.data.validation.Error;
import play.data.validation.Validation;
import play.db.jpa.GenericModel;

import java.util.List;

/**
 * Classe utilitária usada para efetuar as validações feitas através das notações nos atributos de um modelo do play.
 * Ex: @Required, @Max, @Valid, etc.. Importante: as validações são feitas em notações do pacote play.data.validation
 * @author jesse
 *
 */
public class ValidationUtil {

	public static void validate(Object model) {

		Validation validation = Validation.current();

		validation.valid(model);

		List<Error> erros = validation.errors();

		for(Error erro : validation.errors()) {

			if(erro.toString().equals("Validation failed")) {

				erros.remove(erro);

			}

		}

		if (!erros.isEmpty()) {

			throw new ValidationException(erros);

		}

	}

}
