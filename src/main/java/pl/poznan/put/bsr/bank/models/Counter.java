package pl.poznan.put.bsr.bank.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

/**
 * Bank account no base counter class
 * @author Kamil Walkowiak
 */
@Entity("counters")
public class Counter {
    @Id
    private String id;
    @NotNull
    private long seq;

    public Counter() {
    }

    public Counter(String id) {
        this.id = id;
        this.seq = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
