# --- !Ups


UPDATE portal_seguranca.setor SET nome='Gerência de Recursos Minerais' WHERE nome='Gerência de Recursos Hídricos e Minerais';

UPDATE portal_seguranca.setor SET nome='Gerência de Recursos Hídricos' WHERE nome='Gerência de Recursos Humanos';

# --- !Downs

UPDATE portal_seguranca.setor SET nome='Gerência de Recursos Humanos' WHERE nome='Gerência de Recursos Hídricos';

UPDATE portal_seguranca.setor SET nome='Gerência de Recursos Hídricos e Minerais' WHERE nome='Gerência de Recursos Minerais';

