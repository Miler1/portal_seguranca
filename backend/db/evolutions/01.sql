# --- !Ups

CREATE ROLE portal_seguranca LOGIN
ENCRYPTED PASSWORD 'portal_seguranca'
SUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA portal_seguranca;

ALTER SCHEMA portal_seguranca OWNER TO postgres;

--										02.sql

CREATE TABLE portal_seguranca.modulo
(
  id integer NOT NULL,
  alvo character varying(200),
  chave character varying(200),
  data_cadastro timestamp without time zone,
  descricao character varying(500),
  logotipo character varying(200),
  nome character varying(200),
  sigla char(3),
  ips character VARYING(50),
  ativo boolean,
  url character varying(200),
  CONSTRAINT pk_modulo PRIMARY KEY (id)
);

CREATE SEQUENCE portal_seguranca.sq_modulo
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.modulo ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_modulo'::regclass);

COMMENT ON TABLE portal_seguranca.modulo IS 'Esta entidade responsável por armazenar os  modulos (sistemas) que utilizam o portal de segurança.';
COMMENT ON COLUMN portal_seguranca.modulo.id IS 'Identificador único da entidade modulo.';
COMMENT ON COLUMN portal_seguranca.modulo.alvo IS 'Diretiva html para indicar se o módulo abrirá na mesma janela, num popup, numa outra aba, etc.';
COMMENT ON COLUMN portal_seguranca.modulo.chave IS 'Chave de identificação do módulo. É um código único gerado para o módulo conversar com o portal de segurança.';
COMMENT ON COLUMN portal_seguranca.modulo.data_cadastro IS 'Data de cadastro do módulo.';
COMMENT ON COLUMN portal_seguranca.modulo.descricao IS 'Descrição do módulo, utilizada para identificar melhor os módulos cadastrados.';
COMMENT ON COLUMN portal_seguranca.modulo.logotipo IS 'Endereço url do logotipo do módulo.';
COMMENT ON COLUMN portal_seguranca.modulo.nome IS 'Nome do módulo.';
COMMENT ON COLUMN portal_seguranca.modulo.sigla IS 'Sigla do módulo.';
COMMENT ON COLUMN portal_seguranca.modulo.ativo IS 'Indicador do status do módulo (True - Ativo , False - Inativo).';
COMMENT ON COLUMN portal_seguranca.modulo.url IS 'Endereço da aplicação que será chamada para acessar o módulo específico.';

CREATE TABLE portal_seguranca.acao_sistema(
  id INTEGER NOT NULL,
  descricao CHARACTER VARYING(500) NOT NULL,
  permissao CHARACTER VARYING(100),
  CONSTRAINT pk_acao_sistema PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.acao_sistema TO portal_seguranca;

COMMENT ON TABLE portal_seguranca.acao_sistema IS 'Entidade responsável por armazenar as permissões de todas as ações do sistema.';
COMMENT ON COLUMN portal_seguranca.acao_sistema.id IS 'Identificador único da entidade acao_sistema.';
COMMENT ON COLUMN portal_seguranca.acao_sistema.descricao IS 'Descrição da ação (Ex.: Vincular equipe, Encaminhar para aparovação da análise).';
COMMENT ON COLUMN portal_seguranca.acao_sistema.permissao IS 'Permissão do portal de segurança que o usuário logado no sistema deve ter para executar a ação.';


CREATE TABLE portal_seguranca.permissao
(
  id integer NOT NULL,
  codigo character varying(200),
  data_cadastro timestamp without time zone,
  nome character varying(200),
  id_modulo integer NOT NULL,
  CONSTRAINT pk_permissao PRIMARY KEY (id),
  CONSTRAINT fk_p_modulo FOREIGN KEY (id_modulo) REFERENCES portal_seguranca.modulo (id)
);

CREATE SEQUENCE portal_seguranca.sq_permissao
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.permissao ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_permissao'::regclass);

COMMENT ON TABLE portal_seguranca.permissao IS 'Entidade responsável por armazenar as permissões que serão vinculadas aos perfis. Representa uma permissão a uma ação do sistema. Ex.: Supondo que exista a permissão CADASTRAR PROCESSO, os perfis que estiverem associados a essa permissão permitirão aos usuários que o possuem cadastrar processos.';
COMMENT ON COLUMN portal_seguranca.permissao.id IS 'Identificador único da entidade permissao.';
COMMENT ON COLUMN portal_seguranca.permissao.codigo IS 'Código único da permissão. Através deste código, é feita a verificação se o usuário possui ou não a permissão.';
COMMENT ON COLUMN portal_seguranca.permissao.data_cadastro IS 'Data de cadastro da permissão.';
COMMENT ON COLUMN portal_seguranca.permissao.nome IS 'Nome da permissão.';
COMMENT ON COLUMN portal_seguranca.permissao.id_modulo IS 'Identificador da entidade modulo que realizará o relacionamento entre as entidades permissao e modulo. Identifica o modulo (sistema) em que a permissão estará vinculada.';

CREATE TABLE portal_seguranca.perfil
(
  id integer NOT NULL,
  avatar character varying(200),
  data_cadastro timestamp without time zone,
  nome character varying(200),
  CONSTRAINT pk_perfil PRIMARY KEY (id)
);

CREATE SEQUENCE portal_seguranca.sq_perfil
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.perfil ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_perfil'::regclass);

COMMENT ON TABLE portal_seguranca.perfil IS 'Entidade responsável por armazenar os perfis de cada sistema. Um perfil é caracterizado por um conjunto de recursos que podem ser acessados no sistema a partir da entidade Permissão. Um perfil abrange diversas permissões em diversos módulos. No caso de um usuário poder ter vários perfis, ele deverá selecionar um dos perfis ao logar. Pois é o perfil selecionado, que dará as permissões correspondentes ao usuário.';
COMMENT ON COLUMN portal_seguranca.perfil.id IS 'Identificador único da entidade perfil';
COMMENT ON COLUMN portal_seguranca.perfil.avatar IS 'Armazena o caminho da url da imagem que identifica o perfil.';
COMMENT ON COLUMN portal_seguranca.perfil.data_cadastro IS 'Data de cadastro do perfil.';
COMMENT ON COLUMN portal_seguranca.perfil.nome IS 'Nome do perfil.';

CREATE TABLE portal_seguranca.permissao_perfil
(
  id_perfil integer NOT NULL,
  id_permissao integer NOT NULL,
  CONSTRAINT pk_permissao_perfil PRIMARY KEY (id_perfil, id_permissao),
  CONSTRAINT fk_pp_perfil FOREIGN KEY (id_perfil)REFERENCES portal_seguranca.perfil (id) ,
  CONSTRAINT fk_pp_permissao FOREIGN KEY (id_permissao)REFERENCES portal_seguranca.permissao (id)
);

COMMENT ON TABLE portal_seguranca.permissao_perfil IS 'Entidade que relaciona o perfil com suas respectivas permissões. Um perfil vinculado a um módulo poderá ter várias permissões associadas.';
COMMENT ON COLUMN portal_seguranca.permissao_perfil.id_perfil IS 'Identificador da entidade perfil que realizará o relacionamento entre as entidades perfil e rel_perfil_permissao.';
COMMENT ON COLUMN portal_seguranca.permissao_perfil.id_permissao IS 'Identificador da entidade permissao que realizará o relacionamento entre as entidades permissao e rel_perfil_permissao.';

CREATE TABLE portal_seguranca.usuario
(
  id integer NOT NULL,
  data_atualizacao timestamp without time zone,
  data_cadastro timestamp without time zone,
  login character varying(50),
  senha character varying(150),
  email character varying(50),
  ativo boolean,
  trocar_senha boolean,
  CONSTRAINT pk_usuario PRIMARY KEY (id)
);

CREATE SEQUENCE portal_seguranca.sq_usuario
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.usuario ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_usuario'::regclass);

COMMENT ON TABLE portal_seguranca.usuario IS 'Entidade responsável por armazenar os usuários que a partir dos seus perfis terão acesso aos módulos.';
COMMENT ON COLUMN portal_seguranca.usuario.id IS 'Identificador único da entidade usuario.';
COMMENT ON COLUMN portal_seguranca.usuario.data_atualizacao IS 'Data da ultima atualização dos dados do usuário.';
COMMENT ON COLUMN portal_seguranca.usuario.data_cadastro IS 'Data de cadastro do usuário.';
COMMENT ON COLUMN portal_seguranca.usuario.login IS 'Login do usuário.';
COMMENT ON COLUMN portal_seguranca.usuario.senha IS 'Senha do usuário.';
COMMENT ON COLUMN portal_seguranca.usuario.email IS 'Email de contato o usuário.';
COMMENT ON COLUMN portal_seguranca.usuario.ativo IS 'Indicador do status do usuário (True - Ativo , False - Inativo).';
COMMENT ON COLUMN portal_seguranca.usuario.trocar_senha IS 'Indicador para trocar senha (True - Trocar senha, False - Não trocar senha ).';

CREATE TABLE portal_seguranca.perfil_usuario
(
  id_usuario integer NOT NULL,
  id_perfil integer NOT NULL,
CONSTRAINT pk_perfil_usuario PRIMARY KEY (id_usuario, id_perfil),
CONSTRAINT fk_pu_perfil FOREIGN KEY (id_perfil) REFERENCES portal_seguranca.perfil (id),
CONSTRAINT fk_pu_usuario FOREIGN KEY (id_usuario) REFERENCES portal_seguranca.usuario (id)
);

COMMENT ON TABLE portal_seguranca.perfil_usuario IS 'Entidade que representa o relacionamento do usuário com o seu(s) perfil(s). Um usuário poderá ter um perfil ou mais perfis.';
COMMENT ON COLUMN portal_seguranca.perfil_usuario.id_usuario IS 'Identificador da entidade usuario que realizará o relacionamento entre as entidades usuario e rel_usuario_perfil.';
COMMENT ON COLUMN portal_seguranca.perfil_usuario.id_perfil IS 'Identificador da entidade perfil que realizará o relacionamento entre as entidades perfil e rel_usuario_perfil.';

GRANT USAGE ON SCHEMA portal_seguranca TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON ALL TABLES IN SCHEMA portal_seguranca TO portal_seguranca;
GRANT SELECT, USAGE ON ALL SEQUENCES IN SCHEMA portal_seguranca TO portal_seguranca;

--										03.sql

CREATE OR REPLACE FUNCTION portal_seguranca.f_remove_caracter_especial(ptx_texto text)
RETURNS character varying AS
$BODY$DECLARE

	vRESULT TEXT;

BEGIN

    SELECT REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(TRANSLATE(pTX_TEXTO,',.ÁÉÍÓÚÀÈÌÒÙÃÕÂÊÎÔÛÄËÏÖÜÇáéíóúàèìòùãõâêîôôäëïöüçºª°:',
                                               '  AEIOUAEIOUAOAEIOUAEIOUCaeiouaeiouaoaeiooaeiouc    '), '&','E'),'.',' '),'-',''),'/',''),'?','')
    INTO vRESULT;

    RETURN vRESULT;

END;$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

--										04.sql

ALTER TABLE portal_seguranca.modulo ALTER COLUMN alvo TYPE INTEGER USING alvo::INTEGER;

UPDATE portal_seguranca.modulo SET alvo = 0;

ALTER TABLE portal_seguranca.modulo ALTER COLUMN alvo SET NOT NULL;

COMMENT ON COLUMN portal_seguranca.modulo.alvo IS 'Indicar onde o módulo abrirá (0 - Mesma janela; 1 - Outra janela).';

ALTER TABLE portal_seguranca.modulo ADD COLUMN fixo BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN portal_seguranca.modulo.fixo IS 'Indica se o módulo é pré-cadastrado, que não pode ser removido ou desativado (TRUE - Não pode ser removido ou desativado; FALSE - Pode ser removido ou desativado).';

--										05.sql

INSERT INTO portal_seguranca.modulo(id, alvo, chave, data_cadastro, descricao, logotipo, nome, sigla, ativo, url, ips, fixo) VALUES (1, 0, 'CB8DvIIZPlvHjnQeECH1HaEJg5ZxKivn5VALQztoih9VDsCGrR6ZeRMTPuu2ud47', now(), 'Módulo de autenticação e controle de usuários', '1.png', 'Portal de Segurança', 'GAA', true, 'http://localhost:9900/#/inicialPortal', '127.0.0.1,0:0:0:0:0:0:0:1', true);
SELECT setval('portal_seguranca.sq_modulo', 1, true);

INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (1, 'pesquisarUsuarios', now(), 'Pesquisar Usuários', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (2, 'cadastrarUsuario', now(), 'Cadastrar Usuário', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (3, 'removerUsuario', now(), 'Remover Usuário', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (4, 'ativarDesativarUsuario', now(), 'Ativar/Desativar Usuário', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (5, 'editarUsuario', now(), 'Editar Usuário', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (6, 'visualizarUsuario', now(), 'Visualizar Usuário', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (7, 'pesquisarModulos', now(), 'Pesquisar Módulos', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (8, 'cadastrarModulo', now(), 'Cadastrar Módulo', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (9, 'removerModulo', now(), 'Remover Módulo', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (10, 'ativarDesativarModulo', now(), 'Ativar/Desativa Módulo', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (11, 'editarModulo', now(), 'Editar Módulo', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (12, 'visualizarModulo', now(), 'Visualizar Módulo', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (13, 'pesquisarPerfis', now(), 'Pesquisar Perfis', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (14, 'cadastrarPerfil', now(), 'Cadastrar Perfil', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (15, 'removerPerfil', now(), 'Remover Perfil', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (16, 'editarPerfil', now(), 'Editar Perfil', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (17, 'visualizarPerfil', now(), 'Visualizar Perfil', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (18, 'pesquisarPermissoes', now(), 'Pesquisar Permissões', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (19, 'cadastrarPermissao', now(), 'Cadastrar Permissão', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (20, 'removerPermissao', now(), 'Remover Permissão', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (21, 'editarPermissao', now(), 'Editar Permissão', 1);
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (22, 'visualizarPermissao', now(), 'Visualizar Permisso', 1);
SELECT setval('portal_seguranca.sq_permissao', 22, true);

INSERT INTO portal_seguranca.perfil(id, avatar, data_cadastro, nome) VALUES (1, '1.png', now(), 'Administrador');
SELECT setval('portal_seguranca.sq_perfil', 1, true);

INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 1);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 2);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 3);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 4);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 5);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 6);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 7);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 8);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 9);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 10);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 11);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 12);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 13);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 14);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 15);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 16);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 17);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 18);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 19);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 20);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 21);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 22);


--										06.sql

INSERT INTO portal_seguranca.acao_sistema VALUES(1, 'Pesquisar Usurios', 'pesquisarUsuarios');
INSERT INTO portal_seguranca.acao_sistema VALUES(2, 'Cadastrar Usuário', 'cadastrarUsuario');
INSERT INTO portal_seguranca.acao_sistema VALUES(3, 'Remover Usuário', 'removerUsuario');
INSERT INTO portal_seguranca.acao_sistema VALUES(4, 'Ativar/Desativar Usuário', 'ativarDesativarUsuario');
INSERT INTO portal_seguranca.acao_sistema VALUES(5, 'Editar Usuário', 'editarUsuario');
INSERT INTO portal_seguranca.acao_sistema VALUES(6, 'Visualizar Usuário', 'visualizarUsuario');
INSERT INTO portal_seguranca.acao_sistema VALUES(7, 'Pesquisar Módulos', 'pesquisarModulos');
INSERT INTO portal_seguranca.acao_sistema VALUES(8, 'Cadastrar Módulo', 'cadastrarModulo');
INSERT INTO portal_seguranca.acao_sistema VALUES(9, 'Remover Módulo', 'removerModulo');
INSERT INTO portal_seguranca.acao_sistema VALUES(10, 'Ativar/Desativa Módulo', 'ativarDesativarModulo');
INSERT INTO portal_seguranca.acao_sistema VALUES(11, 'Editar Módulo', 'editarModulo');
INSERT INTO portal_seguranca.acao_sistema VALUES(12, 'Visualizar Módulo', 'visualizarModulo');
INSERT INTO portal_seguranca.acao_sistema VALUES(13, 'Pesquisar Perfis', 'pesquisarPerfis');
INSERT INTO portal_seguranca.acao_sistema VALUES(14, 'Cadastrar Perfil', 'cadastrarPerfil');
INSERT INTO portal_seguranca.acao_sistema VALUES(15, 'Remover Perfil', 'removerPerfil');
INSERT INTO portal_seguranca.acao_sistema VALUES(16, 'Editar Perfil', 'editarPerfil');
INSERT INTO portal_seguranca.acao_sistema VALUES(17, 'Visualizar Perfil', 'visualizarPerfil');
INSERT INTO portal_seguranca.acao_sistema VALUES(18, 'Pesquisar Permissões', 'pesquisarPermissoes');
INSERT INTO portal_seguranca.acao_sistema VALUES(19, 'Cadastrar Permissão', 'cadastrarPermissao');
INSERT INTO portal_seguranca.acao_sistema VALUES(20, 'Remover Permissão', 'removerPermissao');
INSERT INTO portal_seguranca.acao_sistema VALUES(21, 'Editar Permissão', 'editarPermissao');
INSERT INTO portal_seguranca.acao_sistema VALUES(22, 'Visualizar Permisso', 'visualizarPermissao');

--										07.sql

GRANT USAGE ON SCHEMA portal_seguranca TO portal_seguranca;

GRANT SELECT ON TABLE portal_seguranca.acao_sistema TO portal_seguranca;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.modulo TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.perfil TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.perfil_usuario TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.permissao TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.permissao_perfil TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.usuario TO portal_seguranca;

GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_modulo TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_perfil TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_permissao TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_usuario TO portal_seguranca;

--										08.sql

ALTER TABLE portal_seguranca.usuario DROP COLUMN trocar_senha RESTRICT;

--										09.sql

ALTER TABLE portal_seguranca.modulo ALTER COLUMN ips TYPE VARCHAR(200);

--                    10.sql

ALTER TABLE portal_seguranca.usuario ADD COLUMN trocar_senha BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN portal_seguranca.usuario.trocar_senha IS 'Indicador para trocar senha (TRUE - Trocar senha; FALSE - Não trocar senha).';


--										11.sql

CREATE TABLE portal_seguranca.motivo
(
	id INTEGER NOT NULL,
	descricao CHARACTER VARYING(50) NOT NULL,
	removido BOOLEAN NOT NULL,
	CONSTRAINT un_motivo UNIQUE (descricao),
	CONSTRAINT pk_motivo PRIMARY KEY (id)
);

COMMENT ON TABLE portal_seguranca.motivo IS 'Entidade responsável por definir o motivo.';
COMMENT ON COLUMN portal_seguranca.motivo.id IS 'Identificador primário da entidade motivo.';
COMMENT ON COLUMN portal_seguranca.motivo.descricao IS 'Identificador único e descrição do motivo.';
COMMENT ON COLUMN portal_seguranca.motivo.removido IS 'Tag para definir se o motivo foi removido ou não.';

INSERT INTO portal_seguranca.motivo(id, descricao, removido) VALUES (1, 'Férias', false);
INSERT INTO portal_seguranca.motivo(id, descricao, removido) VALUES (2, 'Licença médica', false);
INSERT INTO portal_seguranca.motivo(id, descricao, removido) VALUES (3, 'Licença maternidade', false);
INSERT INTO portal_seguranca.motivo(id, descricao, removido) VALUES (4, 'Outro', false);

CREATE TABLE portal_seguranca.agenda_desativacao
(
	id INTEGER NOT NULL,
	id_usuario INTEGER NOT NULL,
	data_inicio DATE,
	data_fim DATE,
	id_motivo INTEGER NOT NULL,
	descricao CHARACTER VARYING (500),
	id_usuario_solicitante INTEGER NOT NULL,
	data_solicitacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_agenda_desativacao PRIMARY KEY (id),
	CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES portal_seguranca.usuario (id),
	CONSTRAINT fk_usuario_solicitante FOREIGN KEY (id_usuario_solicitante) REFERENCES portal_seguranca.usuario (id),
	CONSTRAINT fk_motivo FOREIGN KEY (id_motivo) REFERENCES portal_seguranca.motivo (id)
);

COMMENT ON TABLE portal_seguranca.agenda_desativacao IS 'Entidade responsável por armazenar os dados referentes ao agendamento de desativação de usuários.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.id IS 'Identificador primário da entidade agenda desativação.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.id_usuario IS 'Identificador do usuário que está sendo desativado ou reativado.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.data_inicio IS 'Período inicial em que define o intervalo de desativação do usuário.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.data_fim IS 'Período final em que define o intervalo de reativação do usuário.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.id_motivo IS 'Identificador estrangeiro da entidade motivo.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.descricao IS 'Descrição do motivo da desativação.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.id_usuario_solicitante IS 'Identificador estrangeiro da entidade usuario que referencia o usuário que solicitou o agendamento da desativação.';
COMMENT ON COLUMN portal_seguranca.agenda_desativacao.data_solicitacao IS 'Data da solicitação do agendamento.';

CREATE SEQUENCE portal_seguranca.sq_agenda_desativacao
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.agenda_desativacao ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_agenda_desativacao'::regclass);

GRANT ALL ON TABLE portal_seguranca.agenda_desativacao TO portal_seguranca;
GRANT ALL ON TABLE portal_seguranca.motivo TO portal_seguranca;
GRANT SELECT, USAGE ON portal_seguranca.sq_agenda_desativacao TO portal_seguranca;

--										12.sql

ALTER TABLE portal_seguranca.usuario ADD COLUMN removido BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN portal_seguranca.usuario.removido IS 'Tag para definir se o usuário está removido ou não.';

--										13.sql

CREATE EXTENSION IF NOT EXISTS unaccent SCHEMA public;

--										14.sql

ALTER TABLE portal_seguranca.usuario DROP COLUMN trocar_senha;

CREATE TABLE portal_seguranca.link_acesso(
	id INTEGER NOT NULL,
	id_usuario INTEGER NOT NULL,
	token CHARACTER VARYING(200) NOT NULL,
	redefinir_senha BOOLEAN NOT NULL,
	data_solicitacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT pk_link_acesso PRIMARY KEY (id),
	CONSTRAINT fk_la_usuario FOREIGN KEY (id_usuario) REFERENCES portal_seguranca.usuario (id)
);

CREATE SEQUENCE portal_seguranca.sq_link_acesso
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.link_acesso ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_link_acesso'::regclass);
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_link_acesso TO portal_seguranca;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.link_acesso TO portal_seguranca;


COMMENT ON TABLE portal_seguranca.link_acesso IS 'Entidade responsável por armazenar o controle do link de acesso do usuário.';
COMMENT ON COLUMN portal_seguranca.link_acesso.id IS 'Identificador primário da entidade.';
COMMENT ON COLUMN portal_seguranca.link_acesso.token IS 'Hash gerado, para ser enviado no link de email, para validar o acesso do usuário.';
COMMENT ON COLUMN portal_seguranca.link_acesso.redefinir_senha IS 'Tag para definir se o link é para redefinição de senha.';
COMMENT ON COLUMN portal_seguranca.link_acesso.data_solicitacao IS 'Data de solicitação do link de acesso.';
COMMENT ON COLUMN portal_seguranca.link_acesso.id_usuario IS 'Identificador da entidade usuario que realizará o relacionamento entre as entidades link_acesso e usuario. Identifica o usuário que precisa de serviços de acesso.';

--										15.sql

CREATE TABLE portal_seguranca.reenvio_email(
	id INTEGER NOT NULL,
	id_usuario INTEGER NOT NULL,
	tipo_email INTEGER NOT NULL,
	log TEXT,
	url CHARACTER VARYING (250),
	data_primeira_tentativa DATE,
	CONSTRAINT pk_reenvio_email PRIMARY KEY (id),
	CONSTRAINT fk_re_usuario FOREIGN KEY (id_usuario) REFERENCES portal_seguranca.usuario (id)
);

CREATE SEQUENCE portal_seguranca.sq_reenvio_email
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.reenvio_email ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_reenvio_email'::regclass);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.reenvio_email TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_reenvio_email TO portal_seguranca;

COMMENT ON TABLE portal_seguranca.reenvio_email IS 'Entidade responsável por armazenar o controle do reenvio de email.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.id IS 'Identificador primário da entidade.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.tipo_email IS 'Tipo de email que deve ser reenviado.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.log IS 'Motivo da tentativa de envio de email.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.url IS 'Url de link de acesso enviada em alguns emails.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.data_primeira_tentativa IS 'Data da primeira tentativa de reenvio de email.';
COMMENT ON COLUMN portal_seguranca.reenvio_email.id_usuario IS 'Identificador da entidade usuario que realizará o relacionamento entre as entidades reenvio_email e usuario.';

--										16.sql

INSERT INTO portal_seguranca.perfil(id, avatar, data_cadastro, nome) VALUES (2, '1.png', now(), 'Externo');
SELECT setval('portal_seguranca.sq_perfil', 2, true);

--										17.sql

CREATE TABLE portal_seguranca.cliente_oauth(
	id INTEGER NOT NULL,
	id_cliente CHARACTER VARYING(200) NOT NULL,
	segredo CHARACTER VARYING(200) NOT NULL,
	tipo INTEGER NOT NULL,
	token_acesso CHARACTER VARYING(200),
	token_atualizacao CHARACTER VARYING(200),
	escopo CHARACTER VARYING(50),
	data_solicitacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT ue_co_id_clienete UNIQUE(id_cliente),
	CONSTRAINT ue_co_segredo UNIQUE(segredo),
	CONSTRAINT ue_co_token_acesso UNIQUE(token_acesso),
	CONSTRAINT ue_co_token_atualizacao UNIQUE(token_atualizacao),
	CONSTRAINT pk_cliente_oauth PRIMARY KEY (id)
);

CREATE SEQUENCE portal_seguranca.sq_cliente_oauth
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.cliente_oauth ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_cliente_oauth'::regclass);

COMMENT ON TABLE portal_seguranca.cliente_oauth IS 'Entidade responsável por armazenar o controle de segurança dos módulos.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.id IS 'Identificador único da entidade cliente_oauth';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.id_cliente IS 'Identificador do modulo integrado no protocolo de seguranca oauth2.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.segredo IS 'Secret do modulo integrado no protocolo de seguranca oauth2.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.tipo IS 'Tipo de cliente que o modulo é, CONFIDENCIAL ou PUBLICO.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.token_acesso IS 'Token que representa permissão para acessar recursos externos.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.token_atualizacao IS 'Token para gerar um novo token de acesso.';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.escopo IS 'Escopo da permissão de acesso, "READ" ou "WRITE".';
COMMENT ON COLUMN portal_seguranca.cliente_oauth.data_solicitacao IS 'Data de solicitação do token.';

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.cliente_oauth TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_cliente_oauth TO portal_seguranca;

ALTER TABLE portal_seguranca.modulo DROP COLUMN alvo;
ALTER TABLE portal_seguranca.modulo DROP COLUMN chave;
ALTER TABLE portal_seguranca.modulo ADD COLUMN id_cliente_oauth INTEGER;
ALTER TABLE portal_seguranca.modulo ADD CONSTRAINT fk_m_cliente_oauth FOREIGN KEY(id_cliente_oauth) REFERENCES portal_seguranca.cliente_oauth(id);

COMMENT ON COLUMN portal_seguranca.modulo.id_cliente_oauth IS 'Identificador estrangeiro da tabela cliente_oauth, que relaciona o Modulo ao framework de segurança OAuth';

--										18.sql

CREATE TABLE portal_seguranca.tipo_documento
(
  id INTEGER NOT NULL,
  nome CHARACTER varying(200) NOT NULL,
  CONSTRAINT pk_tipo_documento PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.tipo_documento TO portal_seguranca;

COMMENT ON TABLE portal_seguranca.tipo_documento IS 'Entidade responsável por armazenar os tipos de documentos.';
COMMENT ON COLUMN portal_seguranca.tipo_documento.id IS 'Identificador único da entidade.';
COMMENT ON COLUMN portal_seguranca.tipo_documento.nome IS 'Nome do tipo de documento.';

CREATE TABLE portal_seguranca.documento
(
  id SERIAL NOT NULL,
  id_tipo_documento INTEGER NOT NULL,
  caminho TEXT NOT NULL,
  data_cadastro DATE NOT NULL DEFAULT now(),
  CONSTRAINT pk_documento PRIMARY KEY (id),
  CONSTRAINT fk_d_tipo_documento FOREIGN KEY (id_tipo_documento)
      REFERENCES portal_seguranca.tipo_documento (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.documento TO portal_seguranca;

COMMENT ON TABLE portal_seguranca.documento IS 'Entidade responsável por armazenar os documentos do módulo portal de segurança.';
COMMENT ON COLUMN portal_seguranca.documento.id IS 'Identificador único da entidade.';
COMMENT ON COLUMN portal_seguranca.documento.id_tipo_documento IS 'Identificador único da entidade tipo_documento que realizará o relacionamento entre documento e tipo_documento.';
COMMENT ON COLUMN portal_seguranca.documento.caminho IS 'Path do arquivo.';

INSERT INTO portal_seguranca.tipo_documento VALUES (1, 'ARQUIVO_INTEGRACAO');
INSERT INTO portal_seguranca.tipo_documento VALUES (2, 'IMAGEM_AVATAR');

--										19.sql

ALTER TABLE portal_seguranca.modulo ADD COLUMN cadastrar_perfis BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE portal_seguranca.modulo ADD CONSTRAINT un_modulo_sigla UNIQUE (sigla);

ALTER TABLE portal_seguranca.permissao ADD CONSTRAINT un_permissao_codigo UNIQUE (codigo);

COMMENT ON COLUMN portal_seguranca.modulo.cadastrar_perfis IS 'Tag para definir se o modulo é permitido a cadastrar perfis ou não';

--                    20.sql
INSERT INTO portal_seguranca.tipo_documento VALUES (3, 'LOGOTIPO');

CREATE SEQUENCE portal_seguranca.sq_documento
  NO MINVALUE
  NO MAXVALUE
  START WITH 1
  INCREMENT BY 1
  CACHE 1;

ALTER TABLE portal_seguranca.documento ALTER COLUMN id SET DEFAULT nextval('portal_seguranca.sq_documento'::regclass);

DROP SEQUENCE portal_seguranca.documento_id_seq;

ALTER TABLE portal_seguranca.documento ADD COLUMN nome CHARACTER VARYING (200);

COMMENT ON COLUMN portal_seguranca.documento.nome IS 'Nome do documento.';

ALTER TABLE portal_seguranca.modulo DROP COLUMN fixo;

ALTER TABLE portal_seguranca.modulo DROP COLUMN logotipo;

GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.sq_documento TO portal_seguranca;


--										21.sql

ALTER TABLE portal_seguranca.modulo ADD COLUMN id_documento_logotipo INTEGER;

ALTER TABLE portal_seguranca.modulo ADD CONSTRAINT fk_m_documento FOREIGN KEY (id_documento_logotipo) REFERENCES portal_seguranca.documento (id);

COMMENT ON COLUMN portal_seguranca.modulo.id_documento_logotipo IS 'Identificador da tabela documento que realiza o relacionamento entre as entidades modulo e documento. O logotipo do módulo é salvo como um documento, sendo necessário o relacionamento.';

--										22.sql

ALTER TABLE portal_seguranca.permissao DROP CONSTRAINT un_permissao_codigo;


ALTER TABLE portal_seguranca.permissao ADD CONSTRAINT un_permissao_codigo_modulo UNIQUE(codigo,id_modulo);

--										23.sql

ALTER TABLE portal_seguranca.perfil ADD COLUMN id_modulo_cadastrante INTEGER;

COMMENT ON COLUMN portal_seguranca.perfil.id_modulo_cadastrante IS 'Identificador da entidade modulo, reponsável pelo relacionamento entre as entidades modulo e perfil.Modulo que solicitou o cadastro do perfil.';

ALTER TABLE portal_seguranca.perfil ADD CONSTRAINT fk_p_modulo FOREIGN KEY (id_modulo_cadastrante) REFERENCES portal_seguranca.modulo (id);

UPDATE portal_seguranca.perfil SET id_modulo_cadastrante = (SELECT id FROM portal_seguranca.modulo WHERE sigla = 'GAA') WHERE nome IN ('Administrador', 'Externo');

--										24.sql

INSERT INTO portal_seguranca.documento(id_tipo_documento, caminho, data_cadastro, nome) VALUES (3, '/modulos/logotipo/MCU_logotipo.png', now(), 'MCU_logotipo');

UPDATE portal_seguranca.modulo SET id_documento_logotipo = (SELECT id FROM portal_seguranca.documento WHERE nome = 'MCU_logotipo') WHERE sigla = 'MCU';

--										25.sql

ALTER TABLE portal_seguranca.perfil ADD COLUMN codigo character varying(200);

COMMENT ON COLUMN portal_seguranca.perfil.codigo IS 'Código único do perfil. Através deste código, é feita a verificação se o usuário possui ou não o perfil.';

--										26.sql

UPDATE portal_seguranca.modulo SET id_documento_logotipo = (SELECT id FROM portal_seguranca.documento WHERE nome = 'MCU_logotipo') WHERE sigla = 'GAA';

--										27.sql

ALTER TABLE portal_seguranca.perfil ADD COLUMN ativo BOOLEAN DEFAULT TRUE;

COMMENT ON COLUMN portal_seguranca.perfil.ativo IS 'Tag para saber se o perfil está ativo ou não';

ALTER TABLE portal_seguranca.perfil ADD COLUMN cadastro_entrada_unica BOOLEAN DEFAULT FALSE;

COMMENT ON COLUMN portal_seguranca.perfil.cadastro_entrada_unica IS 'Tag para saber se o perfil foi cadastrado pelo EU, ou se veio de um arquivo de integração de módulos';

ALTER TABLE portal_seguranca.perfil RENAME COLUMN id_modulo_cadastrante TO id_modulo_pertencente;

COMMENT ON COLUMN portal_seguranca.perfil.id_modulo_pertencente IS 'Identificador da entidade modulo, reponsável pelo relacionamento entre as entidades modulo e perfil.Modulo ao qual o perfil pertence.';

--										28.sql

INSERT INTO portal_seguranca.acao_sistema VALUES(23, 'Criar novas credenciais', 'criarNovasCredenciais');
INSERT INTO portal_seguranca.permissao(id, codigo, data_cadastro, nome, id_modulo) VALUES (23, 'criarNovasCredenciais', now(), 'Criar novas credenciais', 1);
INSERT INTO portal_seguranca.permissao_perfil(id_perfil, id_permissao) VALUES (1, 23);

--										29.sql

CREATE TABLE portal_seguranca.acesso_modulo
(
  id integer NOT NULL, 
  id_modulo_servidor integer, 
  id_modulo_cliente integer NOT NULL,
  servico varchar(200) not null,
  enderecos varchar(200),
  data_cadastro date not null,
  CONSTRAINT pk_acesso_modulo PRIMARY KEY (id),
  CONSTRAINT fk_am_id_modulo_servidor FOREIGN KEY (id_modulo_servidor)
      REFERENCES portal_seguranca.modulo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_am_id_modulo_cliente FOREIGN KEY (id_modulo_cliente)
      REFERENCES portal_seguranca.modulo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE portal_seguranca.acesso_modulo
  OWNER TO postgres;
GRANT ALL ON TABLE portal_seguranca.acesso_modulo TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.acesso_modulo TO portal_seguranca;
COMMENT ON TABLE portal_seguranca.acesso_modulo
  IS 'Entidade responsável por armazenar as informações de liberação de acesso à serviços dos módulos.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.id IS 'Identificador único da entidade acao_sistema.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.id_modulo_servidor IS 'Módulo servidor.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.id_modulo_cliente IS 'Módulo cliente requisitante.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.servico IS 'Serviço a ser verificado.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.enderecos IS 'Endereços liberados para acesso, separados por ponto-virgula.';
COMMENT ON COLUMN portal_seguranca.acesso_modulo.data_cadastro IS 'Data de cadastro.';

--                    30.sql

CREATE TABLE portal_seguranca.setor(
 id serial NOT NULL,
 data_cadastro DATE NOT NULL,
 nome VARCHAR(200) NOT NULL,
 sigla VARCHAR(10) NOT NULL,
 tipo_setor INTEGER NOT NULL,
 removido BOOLEAN NOT NULL,
 ativo BOOLEAN NOT NULL,
 id_setor_pai INTEGER,
 CONSTRAINT pk_setor PRIMARY KEY (id),
 CONSTRAINT fk_s_setor_pai FOREIGN KEY (id_setor_pai) REFERENCES portal_seguranca.setor(id)
);

COMMENT ON TABLE portal_seguranca.setor IS 'Entidade responsável por armazenar os setores.';
COMMENT ON COLUMN portal_seguranca.setor.id IS 'Identificador único da entidade setor';
COMMENT ON COLUMN portal_seguranca.setor.nome IS 'Nome do setor.';
COMMENT ON COLUMN portal_seguranca.setor.sigla IS 'Sigla do setor.';
COMMENT ON COLUMN portal_seguranca.setor.tipo_setor IS 'Enumerado que define o tipo de setor. 0 - Secretaria, 1 - Diretoria, 2 - Coordenadoria e 3 - Gerência';
COMMENT ON COLUMN portal_seguranca.setor.removido IS 'Flag que representa se um setor foi excluido ou não.';
COMMENT ON COLUMN portal_seguranca.setor.ativo IS 'Flag que representa se um setor está ativo ou não.';
COMMENT ON COLUMN portal_seguranca.setor.id_setor_pai IS 'Id que faz o relacionamento entre o setor e um setor pai.';

ALTER TABLE portal_seguranca.setor OWNER TO postgres;
GRANT ALL ON TABLE portal_seguranca.setor TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.setor TO portal_seguranca;
GRANT SELECT, USAGE ON SEQUENCE portal_seguranca.setor_id_seq TO portal_seguranca;

CREATE TABLE portal_seguranca.modulo_setor(
 id_modulo INTEGER NOT NULL,
 id_setor INTEGER NOT NULL,
 CONSTRAINT pk_modulo_setor PRIMARY KEY (id_modulo, id_setor),
 CONSTRAINT fk_ms_modulo FOREIGN KEY (id_modulo) REFERENCES portal_seguranca.modulo(id),
 CONSTRAINT fk_ms_setor FOREIGN KEY (id_setor) REFERENCES portal_seguranca.setor(id)
);

COMMENT ON TABLE portal_seguranca.modulo_setor IS 'Entidade que representa o relacionamento do usuário com o seu(s) setores(s). Um usuário poderá ter um setor ou mais setores.';
COMMENT ON COLUMN portal_seguranca.modulo_setor.id_modulo IS 'Identificador da entidade modulo que realizará o relacionamento entre as entidades modulo e modulo_setor.';
COMMENT ON COLUMN portal_seguranca.modulo_setor.id_setor IS 'Identificador da entidade setor que realizará o relacionamento entre as entidades setor e usuario_setor.';

ALTER TABLE portal_seguranca.modulo_setor OWNER TO postgres;
GRANT ALL ON TABLE portal_seguranca.modulo_setor TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.modulo_setor TO portal_seguranca;

CREATE TABLE portal_seguranca.usuario_setor(
 id_usuario INTEGER NOT NULL,
 id_setor INTEGER NOT NULL,
 CONSTRAINT pk_usuario_setor PRIMARY KEY (id_usuario, id_setor),
 CONSTRAINT fk_us_usuario FOREIGN KEY (id_usuario) REFERENCES portal_seguranca.usuario(id),
 CONSTRAINT fk_us_setor FOREIGN KEY (id_setor) REFERENCES portal_seguranca.setor(id)
);

COMMENT ON TABLE portal_seguranca.usuario_setor IS 'Entidade que representa o relacionamento do usuário com o seu(s) setores(s). Um usuário poderá ter um setor ou mais setores.';
COMMENT ON COLUMN portal_seguranca.usuario_setor.id_usuario IS 'Identificador da entidade usuario que realizará o relacionamento entre as entidades usuario e usuario_setor.';
COMMENT ON COLUMN portal_seguranca.usuario_setor.id_setor IS 'Identificador da entidade setor que realizará o relacionamento entre as entidades setor e usuario_setor.';

ALTER TABLE portal_seguranca.usuario_setor OWNER TO postgres;
GRANT ALL ON TABLE portal_seguranca.usuario_setor TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.usuario_setor TO portal_seguranca;

INSERT INTO portal_seguranca.acao_sistema VALUES
 (24, 'Cadastrar Setor', 'cadastrarSetor'),
 (25, 'Pesquisar Setores', 'pesquisarSetores'),
 (26, 'Editar Setor', 'editarSetor'),
 (27, 'Visualizar Setor', 'visualizarSetor'),
 (28, 'Remover Setor', 'removerSetor');

INSERT INTO portal_seguranca.permissao VALUES
 (24, 'cadastrarSetor', now(), 'Cadastrar Setor', 1),
 (25, 'pesquisarSetores', now(), 'Pesquisar Setores', 1),
 (26, 'editarSetor', now(), 'Editar Setor', 1),
 (27, 'visualizarSetor', now(), 'Visualizar Setor', 1),
 (28, 'removerSetor', now(), 'Remover Setor', 1);

INSERT INTO portal_seguranca.permissao_perfil VALUES
 (1, 24),
 (1, 25),
 (1, 26),
 (1, 27),
 (1, 28);


--										31.sql

INSERT INTO portal_seguranca.acao_sistema VALUES
 (29, 'Ativar/Desativar Setor', 'ativarDesativarSetor');

INSERT INTO portal_seguranca.permissao VALUES
 (29, 'ativarDesativarSetor', now(), 'Ativar/Desativar Setor', 1);

INSERT INTO portal_seguranca.permissao_perfil VALUES (1, 29);

--										32.sql

-- Dando permisão ao cadastro_unificado nas novas tabelas de setor

GRANT SELECT ON TABLE portal_seguranca.setor TO cadastro_unificado;

GRANT SELECT ON TABLE portal_seguranca.modulo_setor TO cadastro_unificado;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.usuario_setor TO cadastro_unificado;


--										33.sql

CREATE TABLE portal_seguranca.perfil_setor(
id_perfil INTEGER NOT NULL,
id_setor INTEGER NOT NULL,
CONSTRAINT pk_perfil_setor PRIMARY KEY (id_perfil, id_setor),
CONSTRAINT fk_ps_perfil FOREIGN KEY (id_perfil) REFERENCES portal_seguranca.perfil(id),
CONSTRAINT fk_ps_setor FOREIGN KEY (id_setor) REFERENCES portal_seguranca.setor(id)
);

COMMENT ON TABLE portal_seguranca.perfil_setor IS 'Entidade que representa o relacionamento do perfil com setores(s). Um setor poderá ter um setor ou mais setores.';
COMMENT ON COLUMN portal_seguranca.perfil_setor.id_perfil IS 'Identificador da entidade perfil que realizará o relacionamento entre as entidades perfil e perfil_setor.';
COMMENT ON COLUMN portal_seguranca.perfil_setor.id_setor IS 'Identificador da entidade setor que realizará o relacionamento entre as entidades setor e perfil_setor.';

ALTER TABLE portal_seguranca.perfil_setor OWNER TO postgres;

GRANT ALL ON TABLE portal_seguranca.perfil_setor TO postgres;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal_seguranca.perfil_setor TO portal_seguranca;
GRANT SELECT ON TABLE portal_seguranca.setor TO cadastro_unificado;
GRANT SELECT ON TABLE portal_seguranca.perfil_setor TO cadastro_unificado;


DROP TABLE portal_seguranca.modulo_setor;

--										34.sql

ALTER SEQUENCE portal_seguranca.setor_id_seq RENAME TO sq_setor;


--COMMIT;


# --- !Downs

DROP SCHEMA portal_seguranca CASCADE;