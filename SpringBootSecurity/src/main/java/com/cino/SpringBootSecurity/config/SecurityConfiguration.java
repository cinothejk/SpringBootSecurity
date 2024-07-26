package com.cino.SpringBootSecurity.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	// MY USER DETAILS SERVICE
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authConfig -> {
				authConfig.requestMatchers(HttpMethod.GET, "/").permitAll();
				authConfig.requestMatchers(HttpMethod.GET, "/user").hasAnyAuthority("ROLE_USER");
				authConfig.requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN");
				authConfig.anyRequest().authenticated();
			})
			.csrf(csrf -> csrf.disable())
			// questo si usa in alternativa al Bean sotto
			// per esempio se voglio mettere diversi UserDetails su diverse filterChain
			//.userDetailsService(new MyUserDetailsService())  
			.formLogin(withDefaults()) // Login with browser and Build in Form
			.httpBasic(withDefaults()); // Login with Insomnia or Postman and Basic Auth
		return http.build();
	}
		
	@Bean
	UserDetailsService myUserDetailsService() {
		return new MyUserDetailsService();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	// questi 2 bean sono gli event handler di login success e login failure
	// qui fanno solo una stampa su console
	
	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> successEvent() {
		return event -> {
			System.err.println("Success Login " + event.getAuthentication().getClass().getName() + " - " + event.getAuthentication().getName());
		};
	}
	
	@Bean
	public ApplicationListener<AuthenticationFailureBadCredentialsEvent> failureEvent() {
		return event -> {
			System.err.println("Bad Credentials Login " + event.getAuthentication().getClass().getName() + " - " + event.getAuthentication().getName());
		};
	}
	
	
	
	
	
	
	/*-----------------------------------------------------------------------------------------------------*/
	
	/*
	
	// MULTIPLE SECURITY FILTER CHAIN
	
	// per i permessi su path /user
	
	@Bean
	@Order(100)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/user")			
			.authorizeHttpRequests(authConfig -> {
				authConfig.requestMatchers("/user/**")
					.hasAnyAuthority("ROLE_ADMIN", "ROLE_USER");
				authConfig.anyRequest().authenticated();
			})
			.formLogin(withDefaults());
		return http.build();
	}
	
	
	// per i permessi su path /admin
	
	@Bean
	@Order(101)
	SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/admin")
			.authorizeHttpRequests(authConfig -> {
				authConfig.requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN");
				authConfig.anyRequest().authenticated();
			})
			.formLogin(withDefaults());
		return http.build();
	}
	
	// per i permessi su path /
	
	@Bean
	@Order(102)
	SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/")
			.authorizeHttpRequests(authConfig -> {
				authConfig.anyRequest().permitAll();
			})
			.formLogin(withDefaults());
		return http.build();
	}
	
	// per i permessi su tutti gli altri path
	// IMPORTANTE, questo va sempre messo ultimo come Order (altrimenti blocca quelli successivi)
	
	@Bean
	@Order(103)
	SecurityFilterChain securityFilterChain3(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authConfig -> {
				authConfig.anyRequest().denyAll();
			})
			.formLogin(withDefaults());
		return http.build();
	}
	
	
	/*
	
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
	
	
	*/
	
	
	
	/*-----------------------------------------------------------------------------------------------------*/

	
	// SINGLE SECURITY FILTER CHAIN 
	// IN MEMORY USER DETAILS
	// DEFAULT H2 DATA SOURCE SCHEMA
	
	
	/*
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
			.httpBasic(withDefaults()) // Login with Insomnia or Postman and Basic Auth
			.oauth2Login(withDefaults()); // Login with Google - GitHub - Facebook or .......
		return http.build();
		
	} */
	
	
	
	
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
