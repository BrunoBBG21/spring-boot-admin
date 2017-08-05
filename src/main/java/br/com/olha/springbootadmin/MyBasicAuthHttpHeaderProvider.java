package br.com.olha.springbootadmin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.web.client.HttpHeadersProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBasicAuthHttpHeaderProvider implements HttpHeadersProvider {
    public static final Map cash = new HashMap();
    public static final long DEFAULT_TOKEN_LIFETIME = 60L;

    @Override
    public HttpHeaders getHeaders(Application application) {
        String username = application.getMetadata().get("user.name");
        String password = application.getMetadata().get("user.password");
        String loginUrl = application.getMetadata().get("url.login");
        String tokenLifetime = application.getMetadata().get("token.lifetime"); //seconds

        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            if (StringUtils.hasText(loginUrl)) {
                headers.set(HttpHeaders.AUTHORIZATION, urlCallEncode(username, password, loginUrl, tokenLifetime));

            } else {
                headers.set(HttpHeaders.AUTHORIZATION, encode(username, password));
            }
        }

        return headers;
    }

    protected String encode(String username, String password) {
        String token = Base64Utils
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    protected String urlCallEncode(String username, String password, String loginUrl, String tokenLifetime) {
        String key = loginUrl + username;
        Map map = (Map) cash.get(key);

        if (map == null || timeExpired(map, tokenLifetime)) {
            String token = getToken(username, password, loginUrl);

            map = new HashMap();
            map.put("token", token);
            map.put("time", LocalDateTime.now());
            cash.put(key, map);
        }

        return (String) map.get("token");
    }

    protected Boolean timeExpired(Map map, String tokenLifetime) {
        Long seconds = DEFAULT_TOKEN_LIFETIME;

        try {
            seconds = Long.valueOf(tokenLifetime);
        } catch (Exception e) {}

        LocalDateTime time = (LocalDateTime) map.get("time");
        LocalDateTime endTime = time.plusSeconds(seconds);

        return endTime.isBefore(LocalDateTime.now());
    }

    protected String getToken(String username, String password, String loginUrl) {
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(loginUrl, entity, Map.class);

        String token = (String) responseEntity.getBody().get("token");
        if (token == null) {
            List<String> header = responseEntity.getHeaders().get("Authorization");
            token = header.isEmpty() ? "" : header.get(0);
        }

        return token;
    }
}
