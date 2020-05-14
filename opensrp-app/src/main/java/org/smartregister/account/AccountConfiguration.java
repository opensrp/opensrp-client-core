package org.smartregister.account;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ndegwamartin on 14/05/2020.
 */
public class AccountConfiguration {

    @SerializedName("issuer")
    private String issuerEndpoint;

    @SerializedName("authorization_endpoint")
    private String authorizationEndpoint;

    @SerializedName("token_endpoint")
    private String tokenEndpoint;

    @SerializedName("grant_types_supported")
    private List<String> grantTypesSupported;

    public String getIssuerEndpoint() {
        return issuerEndpoint;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public List<String> getGrantTypesSupported() {
        return grantTypesSupported;
    }
}
