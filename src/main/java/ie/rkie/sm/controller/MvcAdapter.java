package ie.rkie.sm.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcAdapter extends WebMvcConfigurerAdapter {
	
	/**
	 * Provides access to messages stored in Messages_*.properties files.
	 * These are used for internationalisation and for simply returning feedback
	 * from forms etc.
	 * @return
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("Messages");
		return source;
	}

	/**
	 * Defines some straight-through views which do not need controller classes.
	 */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/start").setViewName("start");
        registry.addViewController("/login").setViewName("login");
    }

}
