package com.cino.SpringBootSecurity.config;


import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import static  org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.authorizeHttpRequests(authConfig -> {
				authConfig.requestMatchers(HttpMethod.GET, "/").permitAll();
				authConfig.requestMatchers(HttpMethod.GET, "/user").hasRole("USER");
				authConfig.requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN");
				authConfig.anyRequest().authenticated();
			})
			.csrf(csrf -> csrf.disable())
			.headers().frameOptions().disable()
			.and()
			.formLogin(withDefaults()) // Login with browser and Build in Form
			.httpBasic(withDefaults()); // Login with Insomnia or Postman and Basic Auth
		return http.build();
		
	}
	
	
	@Bean
	JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		return jdbcUserDetailsManager;
	}
		
	
	// QUI puoi configurar qualsiasi DB
	// non essendodci il DEFAULT_USER_SCHEMA_DLL, bisogna creare 
	// un file.sql per creare lo schema del DB
	
	@Bean
    DataSource getDataSource() {
		return DataSourceBuilder.create()
				.driverClassName("org.h2.Driver")
				.url("jdbc:h2:mem:testdb")
				.username("sa")
				.password("")
				.build();
    }	
	
	/*
	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
			.build();
	}
	
	*/
	
	/*
	@Bean
	UserDetailsService userDetailsService() {
		var admin = User.builder()
				.username("Willy De Keyser")
				.password("{noop}password")
				.roles("USER", "ADMIN")
				.build();
		var user = User.builder()
				.username("Ken De Keyser")
				.password("{noop}password")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(admin, user);
	}
	*/
}
