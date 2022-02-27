package services;

import exceptions.PortalSegurancaException;
import models.Perfil;
import models.Usuario;
import portalSema.PessoaPortalSemaVO;
import portalSema.PortalSemaWS;
import utils.Config;

import java.util.HashSet;

public class UsuarioService implements IUsuarioService {

    @Override
    public Usuario BuscaUsuarioEU(String login) {

        Usuario usuario = Usuario.find("login = ? AND removido = false", login).first();

        if (usuario == null || usuario.id == null) {

            return null;

        }

        if(!usuario.ativo) {

            throw new PortalSegurancaException().userMessage("authenticate.inativo");

        }

        return usuario;

    }

    @Override
    public Usuario criarUsuarioByPessoaSema(String acessToken) {

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        PessoaPortalSemaVO pessoaPortalSemaVO = portalSemaWS.recuperaDadosUsuario(acessToken);

        Usuario usuario = new Usuario(pessoaPortalSemaVO);

        if (!usuario.pessoaCadastro) {

            Perfil perfilProdap = Perfil.getPerfilByCodigo(Config.CODIGO_PERFIL_PRODAP);

            usuario.perfis = new HashSet<>();

            usuario.perfis.add(perfilProdap);

            usuario.perfilSelecionado = perfilProdap;

        }

//        Perfil perfilPublico = Perfil.getPerfilByCodigoAndModulo(Config.CODIGO_PERFIL_GESTOR_EU, Modulo.findBySigla(Config.SIGLA_CADASTRO));
//
//        usuario.perfis = new HashSet<>();
//
//        usuario.perfis.add(perfilPublico);

        usuario.save();

        return usuario;

    }

    @Override
    public Usuario atualizarUsuarioByPessoaSema(String acessToken, Usuario usuarioPessoa) {

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        PessoaPortalSemaVO pessoaPortalSemaVO = portalSemaWS.recuperaDadosUsuario(acessToken);

        Usuario usuario = new Usuario(pessoaPortalSemaVO, usuarioPessoa);

        Usuario user = ExternalPessoaService.updatePessoaPortalSema(usuario);

        return user;

    }

}
