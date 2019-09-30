//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.security.openid;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Holds the configuration for an OpenID Connect service.
 *
 * This uses the OpenID Provider URL with the path {@link #CONFIG_PATH} to discover
 * the required information about the OIDC service.
 */
public class OpenIdConfiguration implements Serializable
{
    private static final Logger LOG = Log.getLogger(OpenIdConfiguration.class);
    private static final long serialVersionUID = 2227941990601349102L;
    private static final String CONFIG_PATH = "/.well-known/openid-configuration";

    private final String openIdProvider;
    private final String issuer;
    private final String authEndpoint;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;
    private final List<String> scopes = new ArrayList<>();

    /**
     * Create an OpenID configuration for a specific OIDC provider.
     * @param provider The URL of the OpenID provider.
     * @param clientId OAuth 2.0 Client Identifier valid at the Authorization Server.
     * @param clientSecret The client secret known only by the Client and the Authorization Server.
     */
    public OpenIdConfiguration(String provider, String clientId, String clientSecret)
    {
        this(provider, null, null, clientId, clientSecret);
    }

    /**
     * Create an OpenID configuration for a specific OIDC provider.
     * @param provider The URL of the OpenID provider.
     * @param authorizationEndpoint the URL of the OpenID provider's authorization endpoint if configured.
     * @param tokenEndpoint the URL of the OpenID provider's token endpoint if configured.
     * @param clientId OAuth 2.0 Client Identifier valid at the Authorization Server.
     * @param clientSecret The client secret known only by the Client and the Authorization Server.
     */
    public OpenIdConfiguration(String provider, String authorizationEndpoint, String tokenEndpoint, String clientId, String clientSecret)
    {
        this.openIdProvider = provider;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        if (provider == null)
            throw new IllegalArgumentException("Provider was not configured");

        if (tokenEndpoint == null || authorizationEndpoint == null)
        {
            Map<String, Object> discoveryDocument = fetchOpenIdConnectMetadata(provider);

            this.authEndpoint = (String)discoveryDocument.get("authorization_endpoint");
            if (this.authEndpoint == null)
                throw new IllegalArgumentException("authorization_endpoint");

            this.tokenEndpoint = (String)discoveryDocument.get("token_endpoint");
            if (this.tokenEndpoint == null)
                throw new IllegalArgumentException("token_endpoint");

            if (!Objects.equals(discoveryDocument.get("issuer"), provider))
                LOG.warn("The provider in the metadata is not correct.");
        }
        else
        {
            this.authEndpoint = authorizationEndpoint;
            this.tokenEndpoint = tokenEndpoint;
        }

        issuer = provider;
    }

    private static Map<String, Object> fetchOpenIdConnectMetadata(String provider)
    {
        try
        {
            if (provider.endsWith("/"))
                provider = provider.substring(0, provider.length() - 1);

            URI providerUri = URI.create(provider + CONFIG_PATH);
            InputStream inputStream = providerUri.toURL().openConnection().getInputStream();
            String content = IO.toString(inputStream);
            Map<String, Object> discoveryDocument = (Map)JSON.parse(content);
            if (LOG.isDebugEnabled())
                LOG.debug("discovery document {}", discoveryDocument);

            return discoveryDocument;
        }
        catch (Throwable e)
        {
            throw new IllegalArgumentException("invalid identity provider", e);
        }
    }

    public String getAuthEndpoint()
    {
        return authEndpoint;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public String getOpenIdProvider()
    {
        return openIdProvider;
    }

    public String getTokenEndpoint()
    {
        return tokenEndpoint;
    }

    public void addScopes(String... scopes)
    {
        if (scopes != null)
            Collections.addAll(this.scopes, scopes);
    }

    public List<String> getScopes()
    {
        return scopes;
    }
}
