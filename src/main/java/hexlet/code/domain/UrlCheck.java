package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Basic;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

import static javax.persistence.FetchType.LAZY;

@Entity
public class UrlCheck extends Model {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Getter
    @WhenCreated
    private Timestamp createdAt;

    @Getter
    private int statusCode;

    @Getter
    private String title;

    @Getter
    private String h1;

    @Getter
    @Lob
    @Basic(fetch = LAZY)
    private String description;

    @Getter
    @ManyToOne
    @NotNull
    private Url url;

    public UrlCheck(
            int checkStatusCode,
            String checkTitle,
            String checkH1,
            String checkDescription,
            Url checkUrl) {
        this.statusCode = checkStatusCode;
        this.title = checkTitle;
        this.description = checkDescription;
        this.h1 = checkH1;
        this.url = checkUrl;
    }
}
