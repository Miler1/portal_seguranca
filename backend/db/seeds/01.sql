

# --- !Ups
--1
-- USUARIO INICIAL - ADMINISTRADOR
-- USUARIO: 06926933600
-- SENHA: 123456
INSERT INTO portal_seguranca.usuario(id, data_cadastro, login, senha, email, ativo) VALUES (1, now(), '06926933600', 'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413', 'teste@teste.com', true);
SELECT setval('portal_seguranca.sq_usuario', 2, true);

INSERT INTO portal_seguranca.perfil_usuario(id_usuario, id_perfil) VALUES (1, 1);

--2
---- USUARIOS

INSERT INTO portal_seguranca.usuario (id, data_cadastro, login, senha, email, ativo) VALUES (nextval('portal_seguranca."sq_usuario"'), now(), '80183604059', 'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413', 'email_um@gmail.com', true);
INSERT INTO portal_seguranca.usuario (id, data_cadastro, login, senha, email, ativo) VALUES (nextval('portal_seguranca."sq_usuario"'), now(), '35261537000190', 'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413', 'email_cinco@gmail.com', true);
INSERT INTO portal_seguranca.usuario (id, data_cadastro, login, senha, email, ativo) VALUES (nextval('portal_seguranca."sq_usuario"'), now(), '10419354000129', 'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413', 'email_seis@gmail.com', true);

---- PERFIL DE USUARIOS

INSERT INTO portal_seguranca.perfil_usuario(id_usuario, id_perfil) VALUES (
(SELECT id from portal_seguranca.usuario where login = '80183604059'), 
(SELECT id from portal_seguranca.perfil where nome = 'Administrador'));

INSERT INTO portal_seguranca.perfil_usuario(id_usuario, id_perfil) VALUES (
(SELECT id from portal_seguranca.usuario where login = '35261537000190'), 
(SELECT id from portal_seguranca.perfil where nome = 'Administrador'));

INSERT INTO portal_seguranca.perfil_usuario(id_usuario, id_perfil) VALUES (
(SELECT id from portal_seguranca.usuario where login = '10419354000129'), 
(SELECT id from portal_seguranca.perfil where nome = 'Administrador'));


# --- !Downs
-- 2
---- PERFIL DE USUARIOS

delete from portal_seguranca.perfil_usuario where id_usuario = (SELECT id from portal_seguranca.usuario where login = '80183604059');
delete from portal_seguranca.perfil_usuario where id_usuario = (SELECT id from portal_seguranca.usuario where login = '35261537000190');
delete from portal_seguranca.perfil_usuario where id_usuario = (SELECT id from portal_seguranca.usuario where login = '10419354000129');

---- USUARIOS

delete from portal_seguranca.usuario where id = (SELECT id from portal_seguranca.usuario where login = '80183604059');
delete from portal_seguranca.usuario where id = (SELECT id from portal_seguranca.usuario where login = '35261537000190');
delete from portal_seguranca.usuario where id = (SELECT id from portal_seguranca.usuario where login = '10419354000129');

--1
DELETE FROM portal_seguranca.perfil_usuario WHERE id_usuario = 1 AND id_perfil = 1;

DELETE FROM portal_seguranca.usuario WHERE id = 1;

