package services;

import models.Usuario;
import portalSema.PessoaPortalSemaVO;

import java.io.Serializable;

public interface IUsuarioService extends Serializable {

    Usuario BuscaUsuarioEU(String login);

    Usuario criarUsuarioByPessoaSema(String acessToken);

    Usuario atualizarUsuarioByPessoaSema(String acessToken, Usuario usuario);

}
