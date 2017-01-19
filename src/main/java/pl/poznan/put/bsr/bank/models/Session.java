package pl.poznan.put.bsr.bank.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * User session class
 * @author Kamil Walkowiak
 */
@Entity("sessions")
public class Session {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(name = "sessionId", unique = true)
    private String sessionId;
    @Reference
    @NotNull
    private User user;
    @NotNull
    private String timestamp;

    /**
     * Empty constructor for ORM
     */
    public Session() {
    }

    /**
     * Creates new user session object
     * @param sessionId session id
     * @param user user connected to this session
     */
    public Session(String sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    /*
    Getter and setter methods for session class
     */

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
