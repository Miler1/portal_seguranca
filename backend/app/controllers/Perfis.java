package controllers;

import models.*;
import models.permissoes.AcaoSistema;
import play.mvc.With;
import serializers.PerfilSerializer;
import serializers.UsuariosSerializer;

import java.util.List;

@With(Secure.class)
public class Perfis extends BaseController {

	public static void findAll() {

		renderJSON(Perfil.findAll(), PerfilSerializer.findAll);

	}

	public static void findByFilter(FiltroPerfil filtro) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.PESQUISAR_PERFIS);

		}

		notFoundIfNull(filtro);

		List<Perfil> perfis = Perfil.findByFilter(filtro);
		Long numeroItems = Perfil.countByFilter(filtro);
		Pagination<Perfil> pagination = new Pagination<>(numeroItems, perfis);

		renderJSON(pagination, PerfilSerializer.findByFilter);

	}

	public static void find(Integer id) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.VISUALIZAR_PERFIL);

		}

		notFoundIfNull(id);

		Perfil perfil = Perfil.findById(id);

		perfil.adjustFind();

		renderJSON(perfil, PerfilSerializer.find);

	}

	public static void findByCodigoAndModuleId(String codigoPerfil, Integer idModule) {

		Modulo modulo = Modulo.findById(idModule);

		Perfil perfil = Perfil.getPefilByCodigoWithPermissao(codigoPerfil, modulo);

		renderJSON(perfil, PerfilSerializer.findAllWithPermissoes);

	}

	public static void findUsuariosByPerfil(String codigoPerfil){

		renderJSON(Perfil.findUsuariosByPerfil(codigoPerfil), UsuariosSerializer.findUsuariosByPerfil);
	}

	public static void findUsuariosByPerfilAndSetores(String codigoPerfil, String siglaSetores){

		renderJSON(Perfil.findUsuariosByPerfilAndSetores(codigoPerfil, siglaSetores), UsuariosSerializer.findUsuariosByPerfil);
	}

}
