--Deletar módulos com id entre os valores (71, 74 , 75).
begin;
delete from portal_seguranca.acesso_modulo am where am.id_modulo_cliente in (71, 74 , 75);

delete from portal_seguranca.acesso_modulo am where am.id_modulo_servidor in (71, 74 , 75);

delete from portal_seguranca.modulo_setor rms
using portal_seguranca.setor s, portal_seguranca.modulo m
where RMS.id_setor = s.id
AND rms.id_modulo = m.id AND  m.id in (71, 74 , 75);


delete from portal_seguranca.permissao_perfil rpp
using portal_seguranca.permissao pr
, portal_seguranca.perfil pf
, portal_seguranca.modulo m
where rpp.id_permissao = pr.id
AND rpp.id_perfil = pf.id
AND pr.id_modulo = m.id
AND (pr.id_modulo in (71, 74 , 75)OR pf.id_modulo_cadastrante in (71, 74 , 75)
);

delete from  portal_seguranca.perfil_usuario  pu
using portal_seguranca.usuario u, portal_seguranca.perfil p
where pu.id_usuario = u.id and pu.id_perfil = p.id
and p.id_modulo_cadastrante in (71, 74 , 75);

delete from portal_seguranca.permissao pm where  pm.id_modulo in (71, 74 , 75);

delete from portal_seguranca.perfil p where p.id_modulo_cadastrante in (71, 74 , 75); 

delete from portal_seguranca.modulo m where m.id in (71, 74 , 75);
commit;	