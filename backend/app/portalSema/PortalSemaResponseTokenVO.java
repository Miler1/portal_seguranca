package portalSema;

import java.io.Serializable;

public class PortalSemaResponseTokenVO implements Serializable {

    public String access_token;
    public Integer expires_in;
    public String token_type;
    public String scope;
    public String refresh_token;

}
