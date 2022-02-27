package controllers;

import play.cache.Cache;
import portalSema.PessoaPortalSemaVO;
import portalSema.PortalSemaResponseTokenVO;
import portalSema.PortalSemaWS;

public class PessoaPortalSema extends BaseController {

    public static void recuperaDadosPessoa() {

        String sessionKey = Cache.get("SESSION_KEY", String.class);

        PortalSemaResponseTokenVO portalSemaResponseTokenVO = Cache.get(sessionKey + "_TOKEN", PortalSemaResponseTokenVO.class);

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        PessoaPortalSemaVO pessoaPortalSemaVO = portalSemaWS.recuperaDadosUsuario(portalSemaResponseTokenVO.access_token);

        renderJSON(pessoaPortalSemaVO);

    }

}
