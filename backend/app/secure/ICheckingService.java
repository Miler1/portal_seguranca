package secure;

import portalSema.CheckAuthenticateVO;
import portalSema.CheckCidadaoVO;
import portalSema.PortalSemaResponseTokenVO;

import java.io.Serializable;

public interface ICheckingService extends Serializable {

    CheckCidadaoVO checkCidadao(String cpf);

    CheckAuthenticateVO checkAuthenticate(String accesToken);

    PortalSemaResponseTokenVO checkUserPasswordValidation(String login,String password);
}
