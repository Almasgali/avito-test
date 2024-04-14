package ru.almasgali.avito;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import ru.almasgali.avito.util.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AvitoApplicationTests {

    private static final String URL = "http://localhost:8080";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void testAuthTokens() throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(request("/banner").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, response.statusCode());

        response = httpClient.send(request("/banner").header("token", "user_token").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, response.statusCode());

        response = httpClient.send(request("/banner").header("token", "admin_token").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
    }

    @Test
    void testGetAllBanners() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = httpClient.send(request("/banner").header("token", "admin_token").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        JSONTokener tokener = new JSONTokener(response.body());
        JSONArray jsonArray = new JSONArray(tokener);
        assertEquals(jsonArray.length(), 8);
        for (int i = 0; i < jsonArray.length(); ++i) {
            assertEquals(i + 1, jsonArray.getJSONObject(i).get("id"));
            assertEquals(i + 1, jsonArray.getJSONObject(i).get("feature_id"));
        }
    }

    @Test
    void testGetUserBanner() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> response = httpClient.send(request("/user_banner?tagId=1&featureId=1").header("token", "user_token").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        JSONTokener tokener = new JSONTokener(response.body());
        JSONObject jsonObject = new JSONObject(tokener);
        assertEquals(3, jsonObject.length());
        assertEquals("a", jsonObject.get("text"));
    }

    @Test
    void testGetNonExistingUserBanner() throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(
                request("/user_banner?tagId=3&featureId=17")
                        .header("token", "user_token")
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
    }

    @Test
    void testCreateBanner() throws IOException, InterruptedException, JSONException {

        HttpRequest request = request("/banner")
                .header("token", "admin_token")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"tag_ids\":[11, 17], \"feature_id\":13, \"body\":\"{\\\"text\\\":\\\"abacaba\\\"}\", \"is_active\":false}"))
                .build();
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(request.bodyPublisher());
        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        response = httpClient.send(
                request("/user_banner?tagId=11&featureId=13")
                        .header("token", "admin_token")
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        JSONTokener tokener = new JSONTokener(response.body());
        JSONObject jsonObject = new JSONObject(tokener);
        assertEquals("abacaba", jsonObject.get("text"));
    }

    @Test
    void testDeleteBanner() throws IOException, InterruptedException {
        HttpRequest request = request("/banner/1")
                .header("token", "admin_token")
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.statusCode());
        request = request("/user_banner?tagId=1&featureId=1")
                .header("token", "admin_token")
                .GET()
                .build();
        response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
    }

    @BeforeAll
    public static void init() throws SQLException {
        Util.initDB();
        SpringApplication.run(AvitoApplication.class);
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(URL + path));
    }
}
