package secure.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import play.Logger;
import secure.IAuthenticatedUser;

public class Authorization {

	private static Authorization instance = new Authorization();

	protected Authorization() {}

	public static Authorization getInstance() {

		return instance;

	}

	private final boolean checkRules(String rules, String rulesPackage, IAuthenticatedUser user, Permissible permissible) {

		if(rules == null) {

			return true;

		}

		try {

			boolean satisfied = true;

			for (String rulesOR : rules.split("\\&")) {

				boolean satisfiedOR = false;

				for(String ruleName : rulesOR.split("\\|")) {

					Rule rule = (Rule) Class.forName(rulesPackage + "." + ruleName).newInstance();

					satisfiedOR = satisfiedOR | rule.check(user, permissible);

				}

				satisfied = satisfied & satisfiedOR;

			}

			return satisfied;

		} catch (Exception e) {

			Logger.error(e, "Ocorreu um erro ao verificar a regra da AcaoSistema.");

			return false;

		}

	}

	private final boolean checkRole(String role, IAuthenticatedUser user) {

		if(role == null) {

			return true;

		}

		if (user == null) {

			throw new IllegalArgumentException("Usuário não foi informado para verificação da permissão.");

		}

		return user.hasRole(role);

	}

	public boolean checkPermission(Action action, IAuthenticatedUser user) {

		return checkPermission(action, user, null);

	}

	public boolean checkPermission(Action action, IAuthenticatedUser user, Permissible permissible) {

		if (action == null || action.getId() == null) {

			throw new IllegalArgumentException("AcaoSistema não informada!");

		}

		boolean hasRole = checkRole(action.getRole(), user);
//		boolean satisfyRules = checkRules(action.getRules(), action.getRulesPackage(), user, permissible);

		boolean hasPermission = false;

//		if(action.isRoleOrRules()) {
//
//			hasPermission = hasRole || satisfyRules;
//
//		} else {

//			hasPermission = hasRole && satisfyRules;
			hasPermission = hasRole;
//		}

		if (!hasPermission) {

			Logger.debug("PermissionDenied[SystemAction=" + action.getId() + "]");

			return false;

		}

		return true;

	}

	public List<Integer> checkPermittedActions(List<Action> actions, IAuthenticatedUser user) {

		if (actions == null || actions.isEmpty()) {

			return null;

		}

		List<Integer> idsPermittedActions = new ArrayList<>();

		for (Action action : actions) {

			if (checkPermission(action, user)) {

				idsPermittedActions.add(action.getId());

			}

		}

		return idsPermittedActions;

	}

	public void fillPermittedActions(IAuthenticatedUser user, List<? extends Permissible> permissibles) {

		if (permissibles == null || permissibles.isEmpty()) {

			return;

		}

		for (Permissible permissible : permissibles) {

			fillPermittedActions(user, permissible);

		}

	}

	public void fillPermittedActions(IAuthenticatedUser user, Permissible permissible) {

		List<Integer> permittedActionsIds = new ArrayList<>();

		if ( permissible == null || user == null ) {

			return;

		}

		Collection<Action> actions = permissible.getAvailableActions();

		if ( actions == null || actions.isEmpty()) {

			return;

		}

		for (Action action : actions) {

			Boolean isPermitted = this.checkPermission(action, user, permissible);

			if (isPermitted) {

				permittedActionsIds.add(action.getId());

			}

		}

		permissible.setPermittedActionsIds(permittedActionsIds);

	}

}
