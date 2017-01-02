package azkaban.execregister;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = {"azkaban.execregister"})
@EnableScheduling
@SpringBootApplication
public class Application {
	
	private static SpringApplication springApplication;
	
	private static ApplicationContext ctx;
	
	public static void main(String[] args) throws IOException {
		Object[] configArray = {Application.class, EtcdConfig.class, RestConfig.class, SchedulerConfig.class};
		springApplication = new SpringApplication(configArray);
		ctx = springApplication.run(args);
	}

	public static SpringApplication getSpringApplication() {
		return springApplication;
	}


	public static ApplicationContext getCtx() {
		return ctx;
	}
}
