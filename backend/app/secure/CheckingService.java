package secure;

import portalSema.CheckAuthenticateVO;
import portalSema.CheckCidadaoVO;
import portalSema.PortalSemaResponseTokenVO;
import portalSema.PortalSemaWS;

public class CheckingService implements ICheckingService{

    @Override
    public CheckCidadaoVO checkCidadao(String cpf) {

        if(cpf == null) {

            return null;

        }

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        return portalSemaWS.checarCidadaoCpf(cpf);

    }

    @Override
    public CheckAuthenticateVO checkAuthenticate(String accesToken) {

        if(accesToken == null) {

            return null;

        }

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        return portalSemaWS.checarAutenticacao(accesToken);

    }

    @Override
    public PortalSemaResponseTokenVO checkUserPasswordValidation(String login, String password) {

        if(login == null || password == null){
            return null;
        }

        login = login.replaceAll("[./-]", "");

        PortalSemaWS portalSemaWS = new PortalSemaWS();

        return  portalSemaWS.loginAPI(login, password);
    }


}
