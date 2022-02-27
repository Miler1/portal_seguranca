package unit.secure.authorization.rules;

import secure.IAuthenticatedUser;
import secure.authorization.Permissible;
import secure.authorization.Rule;

public class GrantRuleFake implements Rule {

	@Override
	public boolean check(IAuthenticatedUser user, Permissible permissible) {		
		return true;
	}

}
