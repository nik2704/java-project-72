package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {

    @Getter
    @Id @GeneratedValue
    private long id;

    @Getter
    private String name;

    @Getter
    @WhenCreated
    private Instant createdAt;

    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlChecks;

    public Url(String siteName) {
        this.name = siteName;
    }

    public final long getMaxId() {
        if (getUrlChecks().size() > 0) {
            return getUrlChecks().get(getUrlChecks().size() - 1).getId();
        }

        return -1;
    }

    public final Instant getMaxCheckDate() {
        if (getUrlChecks().size() > 0) {
            return getUrlChecks().get(getUrlChecks().size() - 1).getCreatedAt();
        }

        return null;
    }

}
