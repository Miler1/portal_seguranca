package models;

import common.models.Filter;
import exceptions.PortalSegurancaException;
import org.hibernate.Criteria;

public class FiltroSetor extends Filter {

	public enum Ordenacao implements Order {

		NOME_ASC(OrderDirection.ASC, "nome"),
		NOME_DESC(OrderDirection.DESC, "nome"),
		SIGLA_ASC(OrderDirection.ASC, "sigla"),
		SIGLA_DESC(OrderDirection.DESC, "sigla");

		private OrderDirection orderDirection;
		private String field;

		Ordenacao(OrderDirection orderDirection, String field) {

			this.orderDirection = orderDirection;
			this.field = field;

		}

		@Override
		public OrderDirection getOrderDirection() {

			return this.orderDirection;

		}

		@Override
		public String getField() {

			return this.field;

		}

	}

	public String nome;

	public String sigla;

	public TipoSetor tipo;

	public int numeroPagina;

	public int tamanhoPagina;

	public Ordenacao ordenacao;

	@Override
	public void addOrder(Criteria crit) {

		if(this.ordenacao == null) {

			throw new PortalSegurancaException().userMessage("erro.padrao");

		}

		if(this.ordenacao.getOrderDirection().equals(OrderDirection.ASC)) {

			crit.addOrder(org.hibernate.criterion.Order.asc(this.ordenacao.getField()));

		} else {

			crit.addOrder(org.hibernate.criterion.Order.desc(this.ordenacao.getField()));

		}

	}

}
