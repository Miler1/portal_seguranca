package controllers;

import models.FiltroSetor;
import models.Message;
import models.Pagination;
import models.Setor;
import models.permissoes.AcaoSistema;
import play.i18n.Messages;
import play.mvc.With;
import serializers.SetorSerializer;

import java.util.List;

@With(Secure.class)
public class Setores extends BaseController {

    public static void findAll() {

        renderJSON(Setor.findAll(), SetorSerializer.findAll);

    }

    public static void findAllAtivos() {

        List<Setor> setores = Setor.find("ativo = true AND removido = false").fetch();

        renderJSON(setores, SetorSerializer.findAll);

    }

    public static void create(Setor setor) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.CADASTRAR_SETOR);

        }

        notFoundIfNull(setor);

        setor.save();

        renderJSON(new Message(Messages.get("setores.cadastro.sucesso", setor)));

    }

    public static void findByFilter(FiltroSetor filtro) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.PESQUISAR_SETORES);

        }

        notFoundIfNull(filtro);

        List<Setor> setores = Setor.findByFilter(filtro);
        Long numeroItems = Setor.countByFilter(filtro);
        Pagination<Setor> pagination = new Pagination<>(numeroItems, setores);

        renderJSON(pagination, SetorSerializer.findByFilter);

    }

    public static void find(Integer id) {

        if (!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.VISUALIZAR_SETOR, AcaoSistema.EDITAR_SETOR);
        }

        notFoundIfNull(id);

        Setor setor = Setor.findById(id);

        renderJSON(setor, SetorSerializer.find);

    }


    public static void remove(Integer id) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.REMOVER_SETOR);
        }

        notFoundIfNull(id);

        Setor setor = Setor.findById(id);

        setor.delete();

        renderJSON(new Message("setores.removido.sucesso"));

    }

    public static void update(Setor setor) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.EDITAR_SETOR);
        }

        notFoundIfNull(setor);

        Setor setorSalvo = Setor.findById(setor.id);

        setorSalvo.update(setor);

        renderJSON(new Message(Messages.get("setores.edicao.sucesso", setor)));

    }

    public static void activate(Integer id) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.ATIVAR_DESATIVAR_SETOR);
        }

        notFoundIfNull(id);

        Setor setor = Setor.findById(id);

        setor.activate();

        renderJSON(new Message("setores.ativado.sucesso"));
    }

    public static void deactivate(Integer id) {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.ATIVAR_DESATIVAR_SETOR);
        }

        notFoundIfNull(id);

        Setor setor = Setor.findById(id);

        setor.deactivate();

        renderJSON(new Message("setores.desativado.sucesso"));

    }

    public static void getSetorBySigla(String siglaSetor){

        renderJSON(Setor.findBySigla(siglaSetor),SetorSerializer.find);
    }

    public static void getSiglaSetoresByNivel(String siglaSetor, int nivel){

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.ATIVAR_DESATIVAR_SETOR);
        }

        notFoundIfNull(siglaSetor);


        renderJSON(Setor.getSiglaSetoresByNivel(siglaSetor, nivel), SetorSerializer.find);
    }


    public static void buscaTodosSetores () {

        if(!isExternalResource()) {

            Secure.executeAuthorization(AcaoSistema.PESQUISAR_SETORES);

        }

        List<Setor> setores = Setor.findAll();

        renderJSON(setores, SetorSerializer.findAll);

    }
}