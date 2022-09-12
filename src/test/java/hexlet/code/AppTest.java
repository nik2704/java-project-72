package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;



import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import javax.servlet.http.HttpServletResponse;

import static hexlet.code.utils.Env.UNPROC_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private static final int UNPROC_ENTITY = 422;

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private static Database database;
    private static Transaction transaction;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
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
    public static void afterAll() {
        app.stop();
    }

    @Nested
    class RootTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
            assertThat(response.getBody()).contains("Анализатор страниц");
        }

        @Test
        void testAbout() {
            HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
            assertThat(response.getBody()).contains("Сайты");
        }
    }

    @Nested
    class UrlTest {

        @ParameterizedTest
        @CsvSource(value = {
                "http://www.ya.ru",
                "https://www.ya.ru",
                "http://www.test.ru:8080",
                "https://www.test.ru:80",
                "https://www.test.domain.ru"
            }, ignoreLeadingAndTrailingWhitespace = true)
        void testCorrectPagesCreation(String inputName) {
            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputName)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
            assertThat(body).contains(inputName);
            assertThat(body).contains("Страница успешно добавлена");

            Url actualUrl = new QUrl()
                    .name.equalTo(inputName)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(inputName);
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
            assertThat(body).contains("http://www.ya.ru");
            assertThat(body).contains("Данные URL");
        }

        @Test
        void testIncorrectUrlRequest() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/123456")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
            assertThat(body).contains("Некорректный ID (123456)");
        }

        @Test
        void testAlreadyCreated() {
            String inputName = "http://www.test.test.ru";

            HttpResponse<String> responsePost1 = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputName)
                    .asEmpty();

            assertThat(responsePost1.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);

            HttpResponse<String> responsePost2 = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputName)
                    .asEmpty();

            assertThat(responsePost2.getStatus()).isEqualTo(HttpServletResponse.SC_FOUND);
            assertThat(responsePost2.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(body).contains(inputName);
            assertThat(body).contains("Страница уже существует");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "www.ya.ru",
                "httpsAA://www.ya.ru",
                "http://www.test.ru:8080ABC",
                "https://www.test.ru:-10",
                "https://wwwtestdomainru"
            }, ignoreLeadingAndTrailingWhitespace = true)
        void testWrongPagesCreation(String inputName) {
            HttpResponse<JsonNode> jsonResponse = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputName)
                    .asJson();

            assertThat(jsonResponse.getBody()).isNotNull();
            assertThat(jsonResponse.getBody()).toString().contains("Некорректный URL");
        }
    }
}
