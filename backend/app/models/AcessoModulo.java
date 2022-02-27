package models;

import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.GenericModel;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "portal_seguranca", name = "acesso_modulo")
public class AcessoModulo extends GenericModel {

    @Id
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "id_modulo_servidor", referencedColumnName = "id")
    public Modulo servidor;

    @ManyToOne
    @JoinColumn(name = "id_modulo_cliente", referencedColumnName = "id")
    public Modulo cliente;

    @Column(name = "servico")
    public String servico;

    @Column(name = "enderecos")
    public String enderecos;

    @Column(name = "data_cadastro")
    public Date dataCadastro;


    /**
     * Acesso liberado para o módulo
     * @param servidor - Módulo servidor
     * @param modulosCliente  - Módulo cliente
     * @param servico  - Serviço liberado
     * @param enderecos - Endereco liberado
     * @return
     */
    public static boolean acessoLiberadoParaOModulo(Modulo servidor, Set<Modulo> modulosCliente, String servico, String enderecos) {

        String jpql = "select am from " + AcessoModulo.class.getSimpleName() + " am "
                    + "where am.servidor = :servidor "
                    + "and am.cliente = :cliente "
                    + "and am.servico like :servico "
                    + "and am.enderecos like :enderecos";

        if(StringUtils.isBlank(servico)) {
            servico = "";
        }

        if(StringUtils.isBlank(enderecos)) {
            enderecos = "";
        }

        boolean permitido = false;

        for(Modulo cliente : modulosCliente) {

            Logger.info("isAllowedAccess() -> clientModule: [ %s - %s ]", cliente.id.toString(), cliente.sigla);

            List<AcessoModulo> acessos = AcessoModulo.find(jpql)
                    .setParameter("servidor", servidor)
                    .setParameter("cliente", cliente)
                    .setParameter("servico", "%" + servico + "%")
                    .setParameter("enderecos", "%" + enderecos + "%")
                    .fetch();

            if(!acessos.isEmpty()) {
                permitido = true;
                break;
            }
        }

        return permitido;
    }
}