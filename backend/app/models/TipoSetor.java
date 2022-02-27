package models;

public enum TipoSetor {

    SECRETARIA(0,"Secretaria"),
    DIRETORIA(1,"Diretoria"),
    COORDENADORIA(2,"Coordenadoria"),
    GERENCIA(3,"Gerência");

    public int id;
    public String nome;

    TipoSetor(int id, String nome) {

        this.id = id;
        this.nome = nome;
    }
}