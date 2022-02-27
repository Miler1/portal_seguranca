package models;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
import secure.SHA512Generator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cliente_oauth", schema = "portal_seguranca")
public class OAuthClient extends GenericModel {

	public enum ClientType {
		CONFIDENCIAL,
		PUBLICO
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_cliente_oauth")
	@SequenceGenerator(name = "sq_cliente_oauth", sequenceName = "portal_seguranca.sq_cliente_oauth", allocationSize = 1)
	public Integer id;

	@Required(message = "oAuthClient.clientId.obrigatorio")
	@Column(name = "id_cliente")
	public String clientId;

	@Required(message = "oAuthClient.clientSecret.obrigatorio")
	@Column(name = "segredo")
	public String clientSecret;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo")
	public ClientType clientType;

	@Column(name = "token_acesso")
	public String accessToken;

	@Column(name = "token_atualizacao")
	public String refreshToken;

	@Column(name = "escopo")
	public String scope;

	@Column(name = "data_solicitacao")
	public Date dataSolicitacao;

	@OneToOne(mappedBy = "oAuthClient")
	public Modulo modulo;

	public OAuthClient() {

		super();

		this.dataSolicitacao = new Date();

	}

	public static OAuthClient createOAuthClient() {

		OAuthClient oAuthClient = new OAuthClient();
		oAuthClient.clientId = SHA512Generator.generateValue().replaceAll("-", "");
		oAuthClient.clientSecret = SHA512Generator.generateValue().replaceAll("-", "");
		oAuthClient.clientType = OAuthClient.ClientType.CONFIDENCIAL;
		oAuthClient.dataSolicitacao = new Date();

		return oAuthClient;

	}

}