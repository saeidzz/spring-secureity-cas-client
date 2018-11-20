package com.example.spring.cas.demo.config;

import com.example.spring.cas.demo.service.CustomeUserDetailService;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpSessionEvent;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider casAuthenticationProvider;
    @Autowired
    private AuthenticationEntryPoint casAuthenticationEntryPoint;
    @Autowired
    private ServiceProperties casServiceProperties;

    @Autowired
    CustomeUserDetailService customeUserDetailService;



    /**
     * Configures web based security for specific http requests.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/logoutSuccess").deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        http.addFilter(casAuthenticationFilter()).csrf().disable();
        http.authorizeRequests()
                .antMatchers("/secure/**").hasAnyRole("administrator")
                .antMatchers("/administrator/**").hasAnyRole("administrator")
                 .antMatchers("/free/**").permitAll()
                 .antMatchers("/login/**").authenticated().anyRequest().authenticated();

        http.httpBasic()
                .authenticationEntryPoint(casAuthenticationEntryPoint);

        /*http.logout().logoutSuccessUrl("/logoutSuccess")
                .and()
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                .addFilterBefore(logoutFilter(), LogoutFilter.class);*/


    }

    /**
     * Configures multiple Authentication providers.
     * AuthenticationManagerBuilder allows for easily building multiple authentication mechanisms in the order
     * they're declared.
     * CasAuthenticationProvider is used here.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider);
        auth.userDetailsService(customeUserDetailService);
    }

    /**
     * Cas authentication filter responsible processing a CAS service ticket.
     * Here, I was unable to declare this bean in the Cas configurator class( https://tinyurl.com/y9fzgma9 )
     * @return
     * @throws Exception
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(casServiceProperties);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }




    /**
     * CAS global properties.
     * @return
     */
    @Bean
    public ServiceProperties serviceProperties() {  //http://myIP:18080/mvc-casclient/login-cas
        String appLogin = "http://myIP:8080/login";
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(appLogin);
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * The entry point of Spring Security authentication process (based on CAS).
     * The user's browser will be redirected to the CAS login page.
     * @return
     */
    @Bean
    public AuthenticationEntryPoint casAuthenticationEntryPoint() { // https://myIP:8443/cas/login
        String casLogin = "https://casIP:8443/cas/login";
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casLogin);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    /**
     * CAS ticket validator, if you plan to use CAS 3.0 protocol
     * @return
     */
    @Bean
    public Cas30ServiceTicketValidator ticketValidatorCas30() { // http://myIP:8080/cas
        Cas30ServiceTicketValidator ticketValidator = new
                Cas30ServiceTicketValidator("https://casIP:8443/cas");
        return ticketValidator;
    }

    /**
     * The authentication provider that integrates with CAS.
     * This implementation uses CAS 3.0 protocol for ticket validation.
     *
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidatorCas30());
        // Loads only a default set of authorities for any authenticated users (username and password are)

        provider.setUserDetailsService(getCustomUserDetailService());

        provider.setKey("CAS_PROVIDER_KEY_myIP");

        return provider;
    }

    @Bean
    public UserDetailsService getCustomUserDetailService(){
      return   new com.example.spring.cas.demo.service.impl.CustomUserDetailService();
    }


    //////////single logout

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(
                "http://myIP:8080/logoutSuccess",
                securityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix("https://casIP:8443/cas");
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @EventListener
    public SingleSignOutHttpSessionListener
    singleSignOutHttpSessionListener(HttpSessionEvent event) {
        return new SingleSignOutHttpSessionListener();
    }

}
