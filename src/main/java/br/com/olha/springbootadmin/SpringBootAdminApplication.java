package br.com.olha.springbootadmin;

import de.codecentric.boot.admin.config.EnableAdminServer;
import de.codecentric.boot.admin.web.client.HttpHeadersProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAdminServer
public class SpringBootAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAdminApplication.class, args);
	}

	@Bean
	public HttpHeadersProvider basicAuthFilter() {
		return new MyBasicAuthHttpHeaderProvider();
	}

}
