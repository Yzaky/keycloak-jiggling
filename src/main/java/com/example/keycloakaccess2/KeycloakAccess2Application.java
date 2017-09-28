package com.example.keycloakaccess2;

import java.security.Principal;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.keycloakaccess2.model.Product;
import com.example.keycloakaccess2.model.User;

@SpringBootApplication
public class KeycloakAccess2Application {

    public static void main(String[] args) {
        SpringApplication.run(KeycloakAccess2Application.class, args);

    }

    @Controller
    class Mycontroller

    {

        @GetMapping("/users")
        public String getUsers(Principal principal, Model model) {
            model.addAttribute("principal", principal);
            model.addAttribute("users", Arrays.asList(new User("1", "Youssef", "Zaki"),
                    new User("2", "Michael", "Dell"), new User("3", "John", "Edward")));
            return "users";

        }

        @GetMapping("/logout")
        public String logOut(HttpServletRequest request) throws ServletException {
            request.logout();
            return "/";

        }

        @GetMapping("/products")
        public String getProducts(Principal principal, Model model) {

            model.addAttribute("principal", principal);
            model.addAttribute("products",
                    Arrays.asList(new Product("Toshiba"), new Product("Samsung"), new Product("Sony")));
            return "products";
        }

    }

    @KeycloakConfiguration
    class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
        /**
         * Registers the KeycloakAuthenticationProvider with the authentication manager.
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

            // Adding a Simple Provider because spring security adds a prefix before the role
            // for example in my applicaiton, the role ( Admin ) will be mapped to ROLE_Admin

            KeycloakAuthenticationProvider kp = new KeycloakAuthenticationProvider();
            kp.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
            auth.authenticationProvider(kp);

        }

        /**
         * Defines the session authentication strategy.
         */
        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
        }

        @Bean
        public KeycloakConfigResolver keycloakConfigResolver() {
            // Since we are using a Spring Boot app, we are dealing the the application.properties and not the
            // Keycloak.JSON
            // So we need this method to tell the application to look at the configurations written into our
            // application.properies
            return new KeycloakSpringBootConfigResolver();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            // Accessing to page users by only the Admin,
            // Accessing to page products by Admin and NormalUser
            http.authorizeRequests().antMatchers("/users*").hasRole("Admin").antMatchers("/products*")
                    .hasAnyRole("Admin", "NormalUser");
        }

    }
}
