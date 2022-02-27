package models;

import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "portal_seguranca", name = "motivo")
public class Motivo extends GenericModel {

	public static final Integer FERIAS = 1;
	public static final Integer LICENCA_MEDICA = 2;
	public static final Integer LICENCA_MATERNIDADE = 3;
	public static final Integer OUTRO = 4;

	@Id
	public Integer id;

	public String descricao;

	public Boolean removido;

	public Motivo() {

		super();

		this.removido = false;

	}

	@Override
	public Object _key() {

		return this.id;

	}

}
