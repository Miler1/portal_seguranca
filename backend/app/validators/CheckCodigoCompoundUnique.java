package validators;

import models.Permissao;
import play.data.validation.Check;
import play.i18n.Messages;

public class CheckCodigoCompoundUnique extends Check {

	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {

		Permissao permissao = (Permissao) validatedObject;

		if(permissao.modulo.id != null) {

			if(Permissao.count("FROM Permissao pe JOIN FETCH pe.modulo modulo WHERE modulo.id = " + permissao.modulo.id) == 1) {

				setMessage(Messages.get("permissoes.validacao.codigo.unicoComposto", permissao.codigo));

				return false;

			}

		}

		return true;

	}

}
