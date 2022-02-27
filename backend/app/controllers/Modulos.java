package controllers;

import models.*;
import models.permissoes.AcaoSistema;
import play.mvc.With;
import serializers.ModulosSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@With(Secure.class)
public class Modulos extends BaseController {

	public static void findByFilter(FiltroModulo filtro) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.PESQUISAR_MODULOS);

		}

		notFoundIfNull(filtro);

		List<Modulo> modulos = Modulo.findByFilter(filtro);
		Long numeroItems = Modulo.countByFilter(filtro);
		Pagination<Modulo> pagination = new Pagination<>(numeroItems, modulos);

		renderJSON(pagination, ModulosSerializer.findByFilter);

	}

	public static void validateModuloFile(String fileCode) {

		notFoundIfNull(fileCode);

		Modulo modulo = Modulo.readFileToJson(fileCode);

		modulo.validateModulo();
		modulo.createModuloPerfis();

		renderJSON(modulo, ModulosSerializer.validateModuloFile);

	}

	public static void create(Modulo modulo) {

		if(!isOwnResource()) {

			Secure.executeAuthorization(AcaoSistema.CADASTRAR_MODULO);

		}

		notFoundIfNull(modulo);

		modulo.save();

		renderJSON(modulo, ModulosSerializer.oAuthClientCredentials);

	}

	public static void newCredentials(Integer idModulo) {

		if(!isOwnResource()) {

			Secure.executeAuthorization(AcaoSistema.CRIAR_NOVAS_CREDENCIAIS);

		}

		notFoundIfNull(idModulo);

		Modulo modulo = Modulo.findById(idModulo);

		modulo.newCredentials();

		renderJSON(modulo, ModulosSerializer.oAuthClientCredentials);

	}

	public static void findPerfisUsuarioLogado(Integer idModulo) {
		
		Modulo modulo = Modulo.findById(idModulo);
//		Usuario usuario = Usuario.findById(((Usuario) Secure.getAuthenticatedUser()).id);
		Usuario usuario = Usuario.findByLogin(((Usuario) Secure.getAuthenticatedUser()).login);
		
		renderJSON(modulo.getPerfisUsuarioLogado(usuario), ModulosSerializer.perfisUsuarioLogado);
	
	}

	public static void getLogotipo(Integer id) throws IOException {

		Modulo modulo = Modulo.findById(id);

		notFoundIfNull(modulo);

		renderImage(modulo.getArquivoLogotipo());

	}

	public static void findById(Integer idModulo) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.VISUALIZAR_MODULO);

		}

		notFoundIfNull(idModulo);

		Modulo modulo = Modulo.findById(idModulo);
		modulo.createModuloPerfis();

		renderJSON(modulo, ModulosSerializer.findById);

	}

	public static void buscaTodosModulosComPerfis () {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.PESQUISAR_MODULOS);

		}

		List<Modulo> modulos = Modulo.findAllWithPerfis();

		renderJSON(modulos, ModulosSerializer.findWithPerfis);

	}

	public static void findAll() {

		List<Modulo> modulos = Modulo.findAll();

		renderJSON(modulos, ModulosSerializer.findAll);

	}

	public static void findSetoresModulo(Integer idModulo) {

		Modulo modulo = Modulo.findById(idModulo);
		modulo.createModuloPerfis();

		Set<Setor> setoresModulo = new HashSet<>();

		if (modulo.perfis != null && !modulo.perfis.isEmpty()) {

			for (Perfil perfilModulo : modulo.perfis) {

				setoresModulo.addAll(perfilModulo.setores);
			}
		}

		renderJSON(setoresModulo, ModulosSerializer.setoresModulo);

	}

}
