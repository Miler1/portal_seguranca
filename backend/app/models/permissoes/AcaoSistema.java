package models.permissoes;

import play.db.jpa.GenericModel;
import secure.authorization.Action;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(schema="portal_seguranca", name="acao_sistema")
public class AcaoSistema extends GenericModel implements Action {

	public static final Integer PESQUISAR_USUARIOS = 1;
	public static final Integer CADASTRAR_USUARIO = 2;
	public static final Integer REMOVER_USUARIO = 3;
	public static final Integer ATIVAR_DESATIVAR_USUARIO = 4;
	public static final Integer EDITAR_USUARIO = 5;
	public static final Integer VISUALIZAR_USUARIO = 6;
	public static final Integer PESQUISAR_MODULOS = 7;
	public static final Integer CADASTRAR_MODULO = 8;
	public static final Integer REMOVER_MODULO = 9;
	public static final Integer ATIVAR_DESATIVAR_MODULO = 10;
	public static final Integer EDITAR_MODULO = 11;
	public static final Integer VISUALIZAR_MODULO = 12;
	public static final Integer PESQUISAR_PERFIS = 13;
	public static final Integer CADASTRAR_PERFIL = 14;
	public static final Integer REMOVER_PERFIL = 15;
	public static final Integer EDITAR_PERFIL = 16;
	public static final Integer VISUALIZAR_PERFIL = 17;
	public static final Integer PESQUISAR_PERMISSOES = 18;
	public static final Integer CADASTRAR_PERMISSAO = 19;
	public static final Integer REMOVER_PERMISSAO = 20;
	public static final Integer EDITAR_PERMISSAO = 21;
	public static final Integer VISUALIZAR_PERMISSAO = 22;
	public static final Integer CRIAR_NOVAS_CREDENCIAIS = 23;
	public static final Integer CADASTRAR_SETOR = 24;
	public static final Integer PESQUISAR_SETORES = 25;
	public static final Integer EDITAR_SETOR = 26;
	public static final Integer VISUALIZAR_SETOR = 27;
	public static final Integer REMOVER_SETOR = 28;
	public static final Integer ATIVAR_DESATIVAR_SETOR = 29;

	@Id
	public Integer id;

	public String descricao;

	public String permissao;

	@Transient
	public String pacoteRegras = "models.permissoes.regras";

	@Override
	public Integer getId() {

		return this.id;

	}

	@Override
	public String getDescription() {

		return this.descricao;

	}

	@Override
	public String getRole() {

		return this.permissao;

	}


	@Override
	public int hashCode() {

		final int prime = 31;
		return (id == null) ? 0 : (id.intValue() % prime);

	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;

		}

		if (obj == null || this.id == null || this.getClass() != obj.getClass()) {

			return false;

		}

		AcaoSistema other = (AcaoSistema) obj;

		return this.id.equals(other.id);

	}

	@Override
	public String toString() {

		return " " + id + " - " + descricao;

	}

}
