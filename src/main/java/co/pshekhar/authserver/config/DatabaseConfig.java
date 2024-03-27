package co.pshekhar.authserver.config;

import co.pshekhar.authserver.repository.converter.ReadingConverterForMap;
import co.pshekhar.authserver.repository.converter.WritingConverterForMap;
import io.r2dbc.spi.ConnectionFactory;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcAuditing
@EnableConfigurationProperties({R2dbcProperties.class, FlywayProperties.class})
public class DatabaseConfig extends AbstractR2dbcConfiguration {
    private final ConnectionFactory connectionFactory;

    public DatabaseConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties flywayProperties, R2dbcProperties r2dbcProperties) {
        return Flyway.configure()
                .dataSource(
                        flywayProperties.getUrl(),
                        r2dbcProperties.getUsername(),
                        r2dbcProperties.getPassword()
                )
                .locations(flywayProperties.getLocations().toArray(String[]::new))
                .baselineOnMigrate(true)
                .baselineVersion("1_1")
                .load();
    }

    @Override
    public @NotNull ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Bean
    @Override
    public @NotNull R2dbcCustomConversions r2dbcCustomConversions() {
        List<Object> converters = new ArrayList<>();
        converters.add(new ReadingConverterForMap());
        converters.add(new WritingConverterForMap());
        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }
}
