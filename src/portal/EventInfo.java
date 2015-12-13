package portal;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Created by raja on 11/12/15.
 */
@Table(value = "events")
public class EventInfo implements Serializable {

    @Column
    private Integer userid;

    @PrimaryKey
    private EventKey pk;

    public EventInfo() {
    }

    public EventKey getPk() {
        return pk;
    }

    public void setPk(EventKey pk) {
        this.pk = pk;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Events'{' user='{'0'}'", userid);
    }
}
