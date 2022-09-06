package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Url extends Model {

    @Getter
    @Id @GeneratedValue
    private long id;

    @Getter
    private String name;

    @Getter
    @WhenCreated
    private Timestamp createdAt;

    public Url(String siteName) {
        this.name = siteName;
    }
}
