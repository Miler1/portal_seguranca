package models;

import exceptions.ValidationException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import utils.Identificavel;
import utils.ListUtil;
import utils.ValidationUtil;
import validators.UniqueIfNotRemoved;

import javax.persistence.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(schema = "portal_seguranca", name = "setor")
public class Setor extends GenericModel implements Identificavel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_setor")
    @SequenceGenerator(name = "sq_setor", sequenceName = "portal_seguranca.sq_setor", allocationSize = 1)
    public Integer id;

    @Required(message = "setores.validacao.dataCadastro.req")
    @Column(name = "data_cadastro")
    public Date dataCadastro;

    @Required(message = "setores.validacao.nome.req")
    @UniqueIfNotRemoved(message = "setores.validacao.nome.unico")
    @MaxSize(value = 200)
    @Column
    public String nome;

    @Required(message = "setores.validacao.sigla.req")
    @UniqueIfNotRemoved(message = "setores.validacao.sigla.unico")
    @MaxSize(value = 10)
    @Column
    public String sigla;


    @Required(message="setores.validacao.tipoSetor.req")
    @Column(name="tipo_setor")
    @Enumerated(EnumType.ORDINAL)
    public TipoSetor tipo;

    @Required(message="setores.validacao.removido.req")
    @Column
    public Boolean removido;

    @Required(message="setores.validacao.ativo.req")
    @Column
    public Boolean ativo;

    @ManyToOne
    @JoinColumn(name="id_setor_pai")
    public Setor setorPai;

    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(schema = "portal_seguranca", name = "usuario_setor",
            joinColumns = @JoinColumn(name = "id_setor"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario"))
    public List<Usuario> usuarios;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(schema = "portal_seguranca", name = "perfil_setor",
            joinColumns = @JoinColumn(name = "id_setor", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil", referencedColumnName = "id"))
    public List<Perfil> perfis;

    public Setor() {

        this.dataCadastro = new Date();
        this.removido = false;
        this.ativo = true;
    }

    @Override
    public Setor save() {

        this.dataCadastro = new Date();
        this.removido = false;
        this.ativo = true;

        if(this.perfis!= null && !this.perfis.isEmpty()) {

            this.perfis = Perfil.find("select p from " + Perfil.class.getSimpleName() + " p where p.id in (:ids)")
                    .setParameter("ids", ListUtil.getIds(this.perfis))
                    .fetch();

        }

        if (this.setorPai != null) {

            this.setorPai = Setor.findById(this.setorPai.id);

        }

        ValidationUtil.validate(this);

        this._save();

        return this.refresh();

    }

    public static List<Setor> findByFilter(FiltroSetor filtro) {

        Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Setor.class);
        crit.setFirstResult((filtro.numeroPagina - 1) * filtro.tamanhoPagina);
        crit.setMaxResults(filtro.tamanhoPagina);

        advancedFilterRestrictions(crit, filtro);

        filtro.addOrder(crit);

        return crit.list();

    }

    public static Setor findBySigla(String siglaSetor) {

        Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Setor.class);
        crit.add(Restrictions.eq("sigla", siglaSetor));

        return (Setor) crit.uniqueResult();

    }

    private static void advancedFilterRestrictions(Criteria crit, FiltroSetor filtro) {

        if (filtro.nome != null && !filtro.nome.equals("")) {

            String parametro = Normalizer.normalize(filtro.nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            Criterion nome = Restrictions.sqlRestriction("unaccent({alias}.nome) ILIKE ?", "%" + parametro + "%", new StringType());
            crit.add(nome);

        }

        if (filtro.sigla != null && !filtro.sigla.equals("")) {

            String parametro = Normalizer.normalize(filtro.sigla, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            Criterion sigla = Restrictions.ilike("sigla", parametro, MatchMode.ANYWHERE);
            crit.add(sigla);

        }

        if (filtro.tipo != null) {

            crit.add(Restrictions.eq("tipo", filtro.tipo));
        }

        crit.add(Restrictions.eq("removido", false));

    }

    public static Long countByFilter(FiltroSetor filtro) {

        Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Setor.class);

        advancedFilterRestrictions(crit, filtro);

        return Long.valueOf(crit.list().size());

    }

    @Override
    public Setor delete() {

        this.removido = true;
        this.ativo = false;

        this._save();

        return this.refresh();

    }

    public Setor update(Setor setor) {

        this.nome = setor.nome;
        this.sigla = setor.sigla;
        this.tipo = setor.tipo;

        if (setor.setorPai != null) {

            this.setorPai = Setor.findById(setor.setorPai.id);
        }

        this.perfis.clear();

        if(setor.perfis != null && !setor.perfis.isEmpty()) {

            List<Perfil> perfis = Perfil.find("select p from " + Perfil.class.getSimpleName() + " p where p.id in (:ids)")
                    .setParameter("ids", ListUtil.getIds(setor.perfis))
                    .fetch();

            this.perfis.addAll(perfis);

        }

        ValidationUtil.validate(this);

        this._save();

        return this.refresh();

    }

    public void activate() {

        if (this.removido) {

            throw new ValidationException().userMessage("setores.validacao.ativar.desativar.removido");
        }

        this.ativo = true;

        this._save();
    }

    public void deactivate() {

        if (this.removido) {

            throw new ValidationException().userMessage("setores.validacao.ativar.desativar.removido");
        }

        this.ativo = false;

        this._save();
    }

    @Override
    public Integer getId() {

        return this.id;
    }

    public static List<String> getSiglaSetoresByNivel(String siglaSetor, int nivel){

        List<Setor> setoresEntradaUnica = Setor.findAll();

        Setor setor = setoresEntradaUnica.stream()
                .filter(s -> s.sigla.equals(siglaSetor))
                .collect(Collectors.toList())
                .get(0);

        setoresEntradaUnica = setoresEntradaUnica.stream()
                .filter(s -> s.setorPai != null)
                .collect(Collectors.toList());


        List<Setor> setoresPais = new ArrayList();
        List<Setor> setoresFilhos = new ArrayList();
        List<Setor> resultado = new ArrayList();

        setoresPais.add(setor);

        for (int i = 0; i <= nivel; i++) {

            for (Setor setorPai : setoresPais) {

                setoresFilhos.addAll(getSetoresFilhos(setorPai, setoresEntradaUnica));

            }

            resultado.addAll(setoresFilhos);
            setoresPais.clear();
            setoresPais.addAll(setoresFilhos);
            setoresFilhos.clear();
        }


        List<String> siglas =  new ArrayList();
        for (Setor s : resultado){

            siglas.add(s.sigla);
        }

        return siglas;
    }

    private static List<Setor> getSetoresFilhos(Setor setor, List<Setor> setoresEntradaUnica) {

        return setoresEntradaUnica.stream()
                .filter(s -> s.setorPai.equals(setor))
                .collect(Collectors.toList());

    }
}