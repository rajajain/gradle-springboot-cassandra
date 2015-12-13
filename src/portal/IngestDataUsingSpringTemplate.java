package portal;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by raja on 11/12/15.
 */
@SpringBootApplication
public class IngestDataUsingSpringTemplate {

    private CassandraOperations userCassandraTemplate;

    public IngestDataUsingSpringTemplate() {
    }

    private ApplicationContext applicationContext;

    private void initSpringContext() throws Exception {
        try {


            System.out.println(" uri : " + System.getProperty("user.dir") + " , " + System.getProperty("java.class.path"));
            applicationContext = SpringApplication.run(IngestDataUsingSpringTemplate.class, new String[]{});
            if (applicationContext instanceof AbstractApplicationContext) {
                AbstractApplicationContext abstractAppContext = (AbstractApplicationContext) applicationContext;
                abstractAppContext.registerShutdownHook();
            }
            userCassandraTemplate = (CassandraOperations) applicationContext.getBean("userCassandraTemplate");
        } catch (Throwable e) {
            throw new Exception();
        }
    }

    private void run() throws Exception {
        initSpringContext();
//        userCassandraTemplate = loadCassandraTemplate("user_matrices");
        generateData();
    }

    /**
     * Initialize {CassandraTemplate} manually
     *
     * @return
     */
    private CassandraOperations loadCassandraTemplate(String keySpace) {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder().addContactPoints(InetAddress.getLocalHost()).build();
        } catch (UnknownHostException e) {
        }

        Session session = cluster.connect(keySpace);

        return new CassandraTemplate(session);

    }

    private void generateData() {

        // Prepare the schema
//       session.execute("CREATE TABLE user_matrices.events (eventtype TEXT , userid INT,creationtime BIGINT, PRIMARY KEY(eventtype,creationtime))");

        // Prepare the products hierarchy
        List<EventInfo> events = new ArrayList<EventInfo>();

        String[] eventTypes = {"APP_INSTALL", "SONG_PLAYED_LONG", "ITEM_QUEUED", "ITEM_RENTED",
                "ITEM_LIKED", "CLICK", "ITEM_SHARED", "ITEM_DOWNLOADED", "ITEM_SEARCH"};
        long totalRecords = 10000000;
        int count = 1;
        int skipData = 20;
        while (count <= totalRecords) {
            for (int i = 1; i <= skipData; i++) {
                Random random = new Random();
                int num = random.nextInt(eventTypes.length);
                EventInfo event = new EventInfo();
                EventKey key = new EventKey();
                key.setEventtype(eventTypes[num]);
                key.setCreationtime(System.currentTimeMillis());
                event.setPk(key);
                event.setUserid(i);
                events.add(event);
            }
//            userCassandraTemplate.insert(new Person("1234567890", "David", 40));
            userCassandraTemplate.insert(events);
            count += skipData;
        }


    }

    public static void main(String[] args) {
        IngestDataUsingSpringTemplate app = new IngestDataUsingSpringTemplate();
        try {
            app.run();
        } catch (Exception e) {
            System.out.println("Exception thrown, Something went wrong. Shutting down.");
        }
    }
}
