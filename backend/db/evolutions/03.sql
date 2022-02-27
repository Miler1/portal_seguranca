# --- !Ups

ALTER TABLE portal_seguranca.usuario ADD COLUMN pessoa_cadastro BOOLEAN NOT NULL DEFAULT TRUE;
COMMENT ON COLUMN portal_seguranca.usuario.pessoa_cadastro IS 'Flag que representa se um usuário possui cadastro de pessoa física no Cadastro Unificado';

# --- !Downs

ALTER TABLE portal_seguranca.usuario DROP COLUMN pessoa_cadastro;



