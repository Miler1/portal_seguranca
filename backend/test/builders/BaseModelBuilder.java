package builders;

import play.db.jpa.GenericModel;
import play.db.jpa.JPA;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseModelBuilder<T extends GenericModel> {

	private static Map<Class, Integer> idsPorModel = new HashMap<>();

	protected T model;

	private Class<T> modelClass = getModelClass();

	protected abstract BaseModelBuilder<T> padrao();

	public BaseModelBuilder(T model) {

		this.model = model;

	}

	public T build() {

		return model;

	}

	public T save() {

		T model = build();

		if (model == null) {

			throw new RuntimeException("Para utilizar este método deve-se implementar o método 'build' na factory.");

		}

		model._save();

		return model;

	}

	protected Integer gerarId() {

		if (!idsPorModel.containsKey(this.modelClass)) {

			configurarPrimeiroId();

		}

		Integer id = idsPorModel.get(this.modelClass);
		id++;
		idsPorModel.put(this.modelClass, id);

		return id;

	}

	private Class<T> getModelClass() {

		Type genericSuperclass = this.getClass().getGenericSuperclass();

		if (genericSuperclass instanceof ParameterizedType) {

			ParameterizedType pt = (ParameterizedType) genericSuperclass;
			Type type = pt.getActualTypeArguments()[0];

			return (Class<T>)type;

		}

		throw new IllegalStateException("O builder deve extender a class GenericModel<T> passando a classe T, que é o tipo da model.");

	}

	private void configurarPrimeiroId() {

		String jpql = "select m.id from " + getModelClass().getName() + " m order by m.id desc";
		System.out.println(jpql);

		List<Integer> ids = JPA.em().createQuery(jpql).setMaxResults(1).getResultList();

		if (ids == null || ids.isEmpty() || ids.get(0) == null) {

			idsPorModel.put(modelClass, 0);

		} else {

			idsPorModel.put(modelClass, ids.get(0));

		}

	}

}
