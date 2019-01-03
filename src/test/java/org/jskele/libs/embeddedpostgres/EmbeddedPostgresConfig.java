package org.jskele.libs.embeddedpostgres;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class EmbeddedPostgresConfig {

	@Bean
	EmbeddedPostgresBean embeddedPostgresInitializer() {
		return new EmbeddedPostgresBean();
	}

	@Bean
	@Primary
	DataSourceProperties dataSourceProperties(EmbeddedPostgresBean embeddedPostgresBean) {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setUrl(embeddedPostgresBean.getUrl());
		dataSourceProperties.setInitializationMode(DataSourceInitializationMode.ALWAYS);
		return dataSourceProperties;
	}
}