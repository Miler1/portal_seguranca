package serializers;

import flexjson.JSONSerializer;
import play.Play;

import java.util.Date;

public class SetorSerializer {

    public static JSONSerializer findAll;
    public static JSONSerializer findByFilter;
    public static JSONSerializer find;

    static {

        boolean prettyPrint = Play.mode == Play.Mode.DEV;

        findAll = new JSONSerializer()
                .include(
                        "id",
                        "sigla",
                        "nome"
                )
                .exclude("*")
                .prettyPrint(prettyPrint);

        find = new JSONSerializer()
                .include(
                        "id",
                        "sigla",
                        "nome",
                        "ativo",
                        "setorPai.id",
                        "setorPai.nome",
                        "tipo.id",
                        "tipo.nome",
                        "modulos.nome",
                        "modulos.id",
                        "perfis.id",
                        "perfis.nome",
                        "perfis.codigo"
                )
                .exclude("*")
                .prettyPrint(prettyPrint);

        findByFilter = new JSONSerializer()
                .include(
                        "pageItems.id",
                        "pageItems.nome",
                        "pageItems.sigla",
                        "pageItems.tipo",
                        "pageItems.ativo",
                        "totalItems"
                )
                .exclude("*")
                .transform(DateSerializer.getTransformerWithTimetable(), Date.class)
                .prettyPrint(prettyPrint);
    }
}
