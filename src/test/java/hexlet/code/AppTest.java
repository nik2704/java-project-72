package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Transaction;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;



import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static hexlet.code.utils.Parser.getUrlFormatted;
import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;
    private static MockWebServer mockWebServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        mockWebServer = new MockWebServer();
        String expected = Files.readString(Paths.get("src", "test", "resources", "testSiteDoc.html"));
        mockWebServer.enqueue(new MockResponse().setBody(expected));
        mockWebServer.enqueue(new MockResponse().setBody(expected));
        mockWebServer.start();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockWebServer.shutdown();
    }

//    @Nested
//    class RootTest {
//
//        @Test
//        void testIndex() {
//            HttpResponse<String> response = Unirest.get(baseUrl).asString();
//            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
//            assertThat(response.getBody()).contains("Анализатор страниц");
//        }
//
//        @Test
//        void testAbout() {
//            HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
//            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
//            assertThat(response.getBody()).contains("Сайты");
//        }
//    }

    @Nested
    class UrlTest {

//        @ParameterizedTest
//        @CsvSource(value = {
//                "http://www.ya.ru",
//                "https://www.ya.ru",
//                "http://www.test.ru:8080",
//                "https://www.test.ru:80",
//                "https://www.test.domain.ru"
//            }, ignoreLeadingAndTrailingWhitespace = true)
//        void testCorrectPagesCreation(String inputName) {
//            HttpResponse<String> responsePost = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", inputName)
//                    .asEmpty();
//
//            assertThat(responsePost.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);
//            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");
//
//            HttpResponse<String> response = Unirest
//                    .get(baseUrl + "/urls")
//                    .asString();
//            String body = response.getBody();
//
//            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
//            assertThat(body).contains(inputName);
//            assertThat(body).contains("Страница успешно добавлена");
//
//            Url actualUrl = new QUrl()
//                    .name.equalTo(inputName)
//                    .findOne();
//
//            assertThat(actualUrl).isNotNull();
//            assertThat(actualUrl.getName()).isEqualTo(inputName);
//        }
//
//        @Test
//        void testIncorrectUrlRequest() {
//            HttpResponse<String> response = Unirest
//                    .get(baseUrl + "/urls/123456")
//                    .asString();
//            String body = response.getBody();
//
//            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
//            assertThat(body).contains("Некорректный ID (123456)");
//        }
//
//        @Test
//        void testAlreadyCreated() {
//            String inputName = "http://www.test.test.ru";
//
//            HttpResponse<String> responsePost1 = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", inputName)
//                    .asEmpty();
//
//            assertThat(responsePost1.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);
//
//            HttpResponse<String> responsePost2 = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", inputName)
//                    .asEmpty();
//
//            assertThat(responsePost2.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);
//            assertThat(responsePost2.getHeaders().getFirst("Location")).isEqualTo("/urls");
//
//            HttpResponse<String> response = Unirest
//                    .get(baseUrl + "/urls")
//                    .asString();
//            String body = response.getBody();
//
//            assertThat(body).contains(inputName);
//            assertThat(body).contains("Страница уже существует");
//        }
//
//        @ParameterizedTest
//        @CsvSource(value = {
//                "www.ya.ru",
//                "httpsAA://www.ya.ru",
//                "http://www.test.ru:8080ABC",
//                "https://www.test.ru:-10",
//                "https://wwwtestdomainru"
//            }, ignoreLeadingAndTrailingWhitespace = true)
//        void testWrongPagesCreation(String inputName) {
//            HttpResponse<JsonNode> jsonResponse = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", inputName)
//                    .asJson();
//
//            assertThat(jsonResponse.getBody()).isNotNull();
//            assertThat(jsonResponse.getBody()).toString().contains("Некорректный URL");
//        }

        @Test
        void testCheckUrl() {
            String description = "Description of the site.";
            String title = "Test document";
            String h1 = "Hello, World!";

            String url = mockWebServer.url("/").toString();

            HttpResponse<String> response = Unirest.get(url).asString();

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

            Document body = Jsoup.parse(response.getBody());

            String currentTitle = body.title();
            String currentDescription = null;
            String currentrH1 = null;

            if (body.selectFirst("meta[name=description]") != null) {
                currentDescription = body.selectFirst("meta[name=description]").attr("content");
            }


            if (body.selectFirst("h1") != null) {
                currentrH1 = body.selectFirst("h1").text();
            }

            assertThat(description).isEqualTo(currentDescription);
            assertThat(title).isEqualTo(currentTitle);
            assertThat(h1).isEqualTo(currentrH1);

            String urlTest = getUrlFormatted(url);

            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", urlTest)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);

            Url dbUrl = new QUrl()
                    .name.equalTo(urlTest)
                    .findOne();

            assertThat(dbUrl).isNotNull();

            responsePost = Unirest
                    .post(baseUrl + "/urls/" + dbUrl.getId() + "/checks")
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);


            UrlCheck dbUrlCheck = new QUrlCheck()
                    .url.equalTo(dbUrl)
                    .orderBy()
                        .id
                        .desc()
                    .findOne();

            assertThat(description).isEqualTo(dbUrlCheck.getDescription());
            assertThat(title).isEqualTo(dbUrlCheck.getTitle());
            assertThat(h1).isEqualTo(dbUrlCheck.getH1());
        }
    }
}
