package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    // Note: Configuration properties would need to be injected differently in J2EE
    // These would typically come from @Resource or other J2EE configuration mechanisms
    private String authServerUrl = "https://durvalcrm.org/admin/realms/durval-crm";
    private String publicKeycloakUrl = "https://durvalcrm.org/admin";
    private String internalKeycloakUrl = "https://20.127.155.169:9443";
    private String clientId = "durvalcrm-app";

    // Note: JWT handling would need different implementation in J2EE
    // This is just a placeholder - actual implementation would depend on your J2EE JWT library
    
    private static final HttpClient httpClient = createHttpClient();
    
    private static HttpClient createHttpClient() {
        try {
            // Create a trust manager that accepts all certificates (for development only!)
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            
            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default client if SSL setup fails
            return HttpClient.newHttpClient();
        }
    }

    // Classe para requisição do callback com suporte PKCE
    public static class CallbackRequest {
        public String code;
        public String redirectUri;
        public String codeVerifier; // Campo para PKCE (obrigatório para clientes públicos)
    }

    // Classe para resposta de token
    public static class TokenResponse {
        public String accessToken;
        public String refreshToken;
        public Long expiresIn;
    }

    /**
     * Endpoint público que retorna informações necessárias para o frontend
     * configurar a autenticação OIDC/Keycloak
     */
    @GET
    @Path("/login-info")
    public Map<String, Object> getLoginInfo() {
        String publicAuthServerUrl = publicKeycloakUrl.endsWith("/") ? 
            publicKeycloakUrl + "realms/durval-crm" : 
            publicKeycloakUrl + "/realms/durval-crm";
            
        System.out.printf("Retornando informações de login - publicAuthServerUrl: %s, clientId: %s%n", publicAuthServerUrl, clientId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("authServerUrl", publicAuthServerUrl);
        response.put("clientId", clientId);
        response.put("realm", extractRealmFromUrl(publicAuthServerUrl));
        response.put("loginUrl", buildLoginUrl(publicAuthServerUrl));
        response.put("isPublicClient", true); // Indicador de que é cliente público
        
        return response;
    }

    /**
     * Endpoint para validar token JWT
     * Note: In J2EE, JWT validation would need to be implemented differently
     * This is a simplified version without actual JWT validation
     */
    @GET
    @Path("/validate")
    public Response validateToken() {
        try {
            // Note: In a real J2EE implementation, you would extract and validate the JWT token
            // from the Authorization header and use a JWT library to validate it
            
            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", true);
            validation.put("authenticated", true);
            validation.put("username", "testuser");
            validation.put("sub", "test-user-id");
            validation.put("email", "test@example.com");
            validation.put("name", "Test User");
            validation.put("preferred_username", "testuser");
            validation.put("given_name", "Test");
            validation.put("family_name", "User");
            validation.put("roles", List.of("user"));
            
            // Informações do token (mocked)
            Long currentTime = Instant.now().getEpochSecond();
            validation.put("expires_at", currentTime + 3600); // 1 hour from now
            validation.put("expires_in", 3600);
            validation.put("issued_at", currentTime);
            
            return Response.ok(validation).build();
            
        } catch (Exception e) {
            System.err.printf("Erro ao validar token: %s%n", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("valid", false, "error", "Token inválido ou expirado"))
                    .build();
        }
    }

    /**
     * Endpoint para processar callback OAuth2 e trocar código por token
     * Para clientes públicos com PKCE
     */
    @POST
    @Path("/callback")
    public Response handleCallback(CallbackRequest request) {
        try {
            if (request.code == null || request.codeVerifier == null) {
                System.err.println("Código ou code_verifier ausente na requisição de callback");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Código ou code_verifier obrigatórios para cliente público"))
                        .build();
            }

            System.out.printf("Processando callback com código: %s, redirectUri: %s, PKCE presente: %s%n", 
                    request.code.substring(0, Math.min(10, request.code.length())) + "...",
                    request.redirectUri,
                    "sim");

            // Construir URL do token endpoint usando URL interna (HTTP) para evitar problemas de SSL
            String tokenUrl = internalKeycloakUrl + "/realms/durval-crm/protocol/openid-connect/token";
            System.out.printf("URL do token endpoint: %s%n", tokenUrl);
            
            // Parâmetros para cliente público com PKCE
            String formData = String.format(
                    "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&code_verifier=%s",
                    URLEncoder.encode(request.code, StandardCharsets.UTF_8),
                    URLEncoder.encode(request.redirectUri != null ? request.redirectUri : "", StandardCharsets.UTF_8),
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                    URLEncoder.encode(request.codeVerifier, StandardCharsets.UTF_8)
            );

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> tokenResponse = httpClient.send(tokenRequest, 
                    HttpResponse.BodyHandlers.ofString());

            if (tokenResponse.statusCode() == 200) {
                System.out.println("Token obtido com sucesso");
                return Response.ok(tokenResponse.body()).build();
            } else {
                System.err.printf("Erro ao obter token: %d - %s%n", tokenResponse.statusCode(), tokenResponse.body());
                return Response.status(tokenResponse.statusCode())
                        .entity(Map.of("error", "Falha na autenticação", "details", tokenResponse.body()))
                        .build();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao processar callback");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro interno no servidor"))
                    .build();
        }
    }

    /**
     * Endpoint para logout
     */
    @GET
    @Path("/logout")
    public Map<String, Object> logout() {
        String logoutUrl = authServerUrl + "/protocol/openid-connect/logout" +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&post_logout_redirect_uri=" + URLEncoder.encode("http://localhost:3000", StandardCharsets.UTF_8);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        response.put("logoutUrl", logoutUrl);
        
        return response;
    }

    /**
     * Endpoint para obter informações do usuário
     * Note: In J2EE, this would typically extract user info from JWT token or session
     */
    @GET
    @Path("/user-info")
    public Response getUserInfo() {
        try {
            Map<String, Object> userInfo = new HashMap<>();
            
            // Note: In a real J2EE implementation, you would extract this from JWT token or session
            System.out.println("Retornando informações de usuário de teste (J2EE)");
            userInfo.put("sub", "test-user");
            userInfo.put("username", "testuser");
            userInfo.put("preferred_username", "testuser");
            userInfo.put("email", "test@example.com");
            userInfo.put("email_verified", true);
            userInfo.put("name", "Test User");
            userInfo.put("given_name", "Test");
            userInfo.put("family_name", "User");
            userInfo.put("roles", List.of("user"));
            userInfo.put("resource_access", Map.of());
            
            return Response.ok(userInfo).build();
            
        } catch (Exception e) {
            System.err.printf("Erro ao obter informações do usuário: %s%n", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao obter informações do usuário"))
                    .build();
        }
    }

    /**
     * Endpoint para refrescar token
     */
    @POST
    @Path("/refresh")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response refreshToken(@FormParam("refresh_token") String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Refresh token obrigatório"))
                        .build();
            }

            String tokenUrl = internalKeycloakUrl + "/realms/durval-crm/protocol/openid-connect/token";
            
            String formData = String.format(
                    "grant_type=refresh_token&refresh_token=%s&client_id=%s",
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8)
            );

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> tokenResponse = httpClient.send(tokenRequest, 
                    HttpResponse.BodyHandlers.ofString());

            if (tokenResponse.statusCode() == 200) {
                System.out.println("Token refreshed com sucesso");
                return Response.ok(tokenResponse.body()).build();
            } else {
                System.err.printf("Erro ao fazer refresh do token: %d - %s%n", tokenResponse.statusCode(), tokenResponse.body());
                return Response.status(tokenResponse.statusCode())
                        .entity(Map.of("error", "Falha ao fazer refresh do token"))
                        .build();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao fazer refresh do token");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro interno no servidor"))
                    .build();
        }
    }

    // Métodos auxiliares
    private String extractRealmFromUrl(String authServerUrl) {
        if (authServerUrl != null && authServerUrl.contains("/realms/")) {
            return authServerUrl.substring(authServerUrl.lastIndexOf("/realms/") + 8);
        }
        return "durval-crm";
    }

    private String buildLoginUrl(String serverUrl) {
        return serverUrl + "/protocol/openid-connect/auth" +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=openid profile email" +
                "&code_challenge_method=S256"; // PKCE obrigatório para clientes públicos
    }
}