package unit.secure.authorization.rules;

import java.util.List;

import secure.authorization.Action;
import secure.authorization.Permissible;

public class PermissibleFake implements Permissible {
	
	@Override
	public List<Action> getAvailableActions() {
		return null;
	}

	@Override
	public void setPermittedActionsIds(List<Integer> actionsIds) {
					
	}

	@Override
	public List<Integer> getPermittedActionsIds() {
		return null;
	}
	
}