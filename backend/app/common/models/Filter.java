package common.models;

import org.hibernate.Criteria;

public abstract class Filter {

	public enum OrderDirection {
		ASC,
		DESC
	}

	public interface Order {

		OrderDirection getOrderDirection();
		String getField();

	}

	public abstract void addOrder(Criteria crit);

}
