package com.altruist.config

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import javax.sql.DataSource
import java.sql.SQLException

@Configuration
class RepositoryConfiguration {

    @Value('${test-db.port}')
    int port;

    @Profile("test")
    @Bean(destroyMethod = "close")
    Sql sql(DataSource dataSource) throws SQLException {
        return new Sql(dataSource)
    }

    @Profile("test")
    @Bean
    DataSource dataSource() {
        EmbeddedPostgres embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(port)
                .start();
        return embeddedPostgres.getPostgresDatabase();
    }
}
