package models;

import play.db.jpa.GenericModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reenvio_email", schema = "portal_seguranca")
public class ReenvioEmail extends GenericModel {

	public enum TipoEmail {
		PRIMEIRO_ACESSO,
		CRIAR_SENHA_PRIMEIRO_ACESSO,
		REDEFINIR_SENHA,
		SENHA_REDEFINIDA
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario")
	@SequenceGenerator(name = "sq_usuario", sequenceName = "portal_seguranca.sq_usuario", allocationSize = 1)
	public Integer id;

	@OneToOne
	@JoinColumn(name = "id_usuario", referencedColumnName = "id")
	public Usuario usuario;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_email")
	public TipoEmail tipoEmail;

	public String log;

	public String url;

	@Column(name = "data_primeira_tentativa")
	@Temporal(value = TemporalType.DATE)
	public Date dataPrimeiraTentativa;

	@Transient
	public static final Integer nroMaxTentativas = 3;

	public ReenvioEmail() {

		super();

		this.dataPrimeiraTentativa = new Date();

	}

	public ReenvioEmail(String log, Usuario usuario, String url, ReenvioEmail.TipoEmail tipoEmail) {

		super();

		this.dataPrimeiraTentativa = new Date();
		this.usuario = usuario;
		this.log = log;
		this.url = url;
		this.tipoEmail = tipoEmail;

	}

}