package portalSema;

import java.util.Date;

public class PessoaPortalSemaVO {

    public class Contato {
        public Integer id;
        public String email_anterior;
        public String email;
        public String email_verificado;
        public String telefone;
        public String celular;
        public String whatsapp;
        public String facebook;
        public String twitter;
        public String instagram;
    }

    public class Pessoa {
        public String nome;
        public String cpf;
        public String sexo;
        public Date data_nascimento;
        public Contato contato;
        public Date created_at;
        public Date updated_at;
    }

    public Pessoa pessoa;

}
