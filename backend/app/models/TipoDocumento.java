package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;
import utils.Config;

import java.io.File;

@Entity
@Table(schema="portal_seguranca", name="tipo_documento")
public class TipoDocumento extends GenericModel {

	public enum Tipos {

		ARQUIVO_INTEGRACAO(1, "modulos" + File.separator + "integracao", "arquivo_integracao"),
		IMAGEM_AVATAR(2, "perfis" + File.separator + "avatar", "imagem_avatar"),
		LOGOTIPO(3, "modulos" + File.separator + "logotipo", "logotipo");

		public final Integer id;
		public final String caminho;
		public final String prefixo;

		Tipos(Integer id, String caminho, String prefixo) {

			this.id = id;
			this.caminho = caminho;
			this.prefixo = prefixo;

		}

		public static Tipos get(Integer id) {

			for(Tipos tipo : values()) {

				if(tipo.id.equals(id)) {

					return tipo;

				}

			}

			return null;

		}

	}

	@Id
	@Column(name="id")
	public Integer id;

	@Column(name="nome")
	public String nome;

	public Tipos getTipo() {

		return Tipos.get(this.id);

	}

}
