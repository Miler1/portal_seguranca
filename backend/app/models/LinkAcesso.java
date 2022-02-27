package models;

import exceptions.ValidationException;
import org.apache.commons.lang.time.DateUtils;
import play.Play;
import play.db.jpa.GenericModel;
import secure.SHA512Generator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "link_acesso", schema = "portal_seguranca")
public class LinkAcesso extends GenericModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_link_acesso")
	@SequenceGenerator(name = "sq_link_acesso", sequenceName = "portal_seguranca.sq_link_acesso", allocationSize = 1)
	public Integer id;

	@OneToOne
	@JoinColumn(name = "id_usuario", referencedColumnName = "id")
	public Usuario usuario;

	public String token;

	@Column(name = "redefinir_senha")
	public Boolean redefinirSenha;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "data_solicitacao")
	public Date dataSolicitacao;

	public LinkAcesso() {

		super();

		this.dataSolicitacao = new Date();

	}

	public LinkAcesso(Usuario usuario, Boolean redefinirSenha) {

		super();

		this.dataSolicitacao = new Date();
		this.redefinirSenha = redefinirSenha;
		this.token = SHA512Generator.generateValue(usuario.login + Long.toString(this.dataSolicitacao.getTime()) + Math.random());
		this.usuario = usuario;

	}

	public static LinkAcesso findByToken(String token) {

		LinkAcesso linkAcesso = LinkAcesso.find("token", token).first();

		if(linkAcesso == null || linkAcesso.usuario == null) {

			throw new ValidationException().userMessage("usuarios.token.tokenInvalido");

		} else {

			if(linkAcesso.usuario.senha != null && !linkAcesso.redefinirSenha) {

				throw new ValidationException().userMessage("usuarios.criarSenhaPrimeiroAcesso.senha");

			}

			if(linkAcesso.redefinirSenha) {

				verifyExpirationTime(linkAcesso.dataSolicitacao);

			}

		}

		return linkAcesso;

	}

	private static void verifyExpirationTime(Date data) {

		Date horaMaxima = DateUtils.addHours(data, Integer.parseInt(Play.configuration.getProperty("token.expirationTime")));

		if(horaMaxima.before(new Date())) {

			throw new ValidationException().userMessage("usuarios.token.expirado");

		}

	}

}
