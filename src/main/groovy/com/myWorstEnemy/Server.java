package com.myWorstEnemy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@EnableAutoConfiguration
@SpringBootApplication
public class Server extends SpringBootServletInitializer
{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(Server.class);
	}

	public static void main(String[] args)
	{
		SpringApplication.run(Server.class, args);
	}
}
