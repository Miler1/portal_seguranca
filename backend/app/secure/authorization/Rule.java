package secure.authorization;

import secure.IAuthenticatedUser;

public interface Rule {

	boolean check(IAuthenticatedUser user, Permissible permissible);

}
