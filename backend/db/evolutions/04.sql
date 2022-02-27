
# --- !Ups

INSERT INTO portal_seguranca.perfil (data_cadastro, nome, id_modulo_pertencente, codigo, ativo, cadastro_entrada_unica)
VALUES (now(), 'Prodap', null, 'PRODAP', true, false);

ALTER TABLE portal_seguranca.permissao ALTER COLUMN id_modulo DROP NOT NULL;

INSERT INTO portal_seguranca.permissao (id, codigo, data_cadastro, nome, id_modulo)
VALUES (290, 'cadastrarPessoaFisicaProdap', now(), 'Cadastrar Pessoa Física Prodap', null);

INSERT INTO portal_seguranca.permissao_perfil (id_perfil, id_permissao) VALUES
((SELECT DISTINCT id FROM portal_seguranca.perfil WHERE codigo = 'PRODAP'),
 (SELECT DISTINCT id FROM portal_seguranca.permissao WHERE codigo = 'cadastrarPessoaFisicaProdap'));


# --- !Downs

DELETE FROM portal_seguranca.permissao_perfil WHERE
id_perfil = (SELECT DISTINCT id FROM portal_seguranca.perfil WHERE codigo = 'PRODAP') AND
id_permissao = (SELECT DISTINCT id FROM portal_seguranca.permissao WHERE codigo = 'cadastrarPessoaFisicaProdap');

DELETE FROM portal_seguranca.permissao WHERE codigo = 'cadastrarPessoaFisicaProdap';

ALTER TABLE portal_seguranca.permissao ALTER COLUMN id_modulo SET NOT NULL;

DELETE FROM portal_seguranca.perfil WHERE codigo = 'PRODAP';
