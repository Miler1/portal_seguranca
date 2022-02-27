package secure.authorization;

import java.util.List;

public interface Permissible {

	List<Action> getAvailableActions();

	void setPermittedActionsIds(List<Integer> actionsIds);

	List<Integer> getPermittedActionsIds();

}
