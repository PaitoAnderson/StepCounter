package com.paitoanderson.stepcounter.api;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by Paito Anderson on 14-07-30.
 */
public class FitbitApi extends DefaultApi10a {

    public static final int ApiVersion = 1;
    public static final String ApiBase = "api.fitbit.com";

    public static final String Key = "e1244032a7934da1a79106727d77581f";
    public static final String Secret = "b801bc4a968c460e8d62b168a5e2cb48";
    public static final String CallbackUrl = "http://paitoanderson.com/oauth/fitbit";

    public FitbitApi() {
        super();
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "https://api.fitbit.com/oauth/request_token";
    }

    @Override
    public String getAuthorizationUrl(Token token) {
        return String.format("https://www.fitbit.com/oauth/authorize?oauth_token=%s", token.getToken());
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.fitbit.com/oauth/access_token";
    }
}
