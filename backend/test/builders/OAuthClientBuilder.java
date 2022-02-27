package builders;

import models.OAuthClient;
import secure.SHA512Generator;

import java.util.Date;

public class OAuthClientBuilder extends BaseModelBuilder<OAuthClient> {


	public OAuthClientBuilder() {

		super(new OAuthClient());

		padrao();

	}

	public OAuthClientBuilder(OAuthClient model) {

		super(model);

	}

	@Override
	protected OAuthClientBuilder padrao() {

		this.model.clientId = SHA512Generator.generateValue().replaceAll("-", "");
		this.model.clientSecret = SHA512Generator.generateValue().replaceAll("-", "");
		this.model.clientType = OAuthClient.ClientType.CONFIDENCIAL;
		this.model.dataSolicitacao = new Date();

		return this;

	}

	public OAuthClientBuilder comAccessToken() {

		this.model.accessToken = SHA512Generator.generateValue();

		return this;

	}

	public OAuthClientBuilder comRefreshToken() {

		this.model.refreshToken = SHA512Generator.generateValue();

		return this;

	}

	public OAuthClientBuilder comScope(String scope) {

		this.model.scope = scope;

		return this;

	}

}
