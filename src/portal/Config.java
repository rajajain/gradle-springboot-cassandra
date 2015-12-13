package portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;

/**
 * Created by raja on 02/12/15.
 */
@Configuration
public class Config {

    @Autowired
    private ConfigFile properties;

    public ConfigFile getProperties() {
        return properties;
    }

    @Bean(name = "properties")
    public static ConfigFile properties() {
        ConfigFile ppc = new ConfigFile();
        ClassPathResource[] resources = new ClassPathResource[]
                {new ClassPathResource("server.properties")};
        ppc.setLocations(resources);
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc;
    }

    @Bean(name = "cassandraCluster")
    public CassandraClusterFactoryBean cassandraCluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(properties.getStringProperty("cassandra.contactpoints", "localhost"));
        cluster.setPort(properties.getIntProperty("cassandra.port", 9042));
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMappingContext() {
        return new BasicCassandraMappingContext();
    }

    @Bean
    public CassandraConverter cassandraConverter() {
        return new MappingCassandraConverter(cassandraMappingContext());
    }

    @Bean
    public CassandraSessionFactoryBean cassandraSession() throws Exception {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cassandraCluster().getObject());
        session.setKeyspaceName(properties.getStringProperty("cassandra.user.keyspace", "user_matrices"));
        session.setConverter(cassandraConverter());
        session.setSchemaAction(SchemaAction.NONE);
        return session;
    }

    @Bean(name = "userCassandraTemplate")
    public CassandraOperations userCassandraTemplate() throws Exception {
        return new CassandraTemplate(cassandraSession().getObject());
    }


}
