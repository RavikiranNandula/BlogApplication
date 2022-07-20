package com.blogApplication.security.configuration;
import com.blogApplication.model.Posts;
import com.blogApplication.repository.PostsRepositry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Date;
import java.util.List;

@Configuration
@EnableWebMvc
public class securityConfiguration{
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers("api/posts/**","/css/**","//**").permitAll()
                .antMatchers("/v3/api-docs","/v2/api-docs","/swagger-resources/**","/swagger-ui/**","/webjars/**"). permitAll()
                .antMatchers("/editpost/**","/newpost","/delete/**").authenticated()
                .and()
                .formLogin().loginPage("/loginPage").loginProcessingUrl("/processLogin")
                .and().httpBasic()
                .and().logout().invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").and().csrf().disable();
        return http.build();
   }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
