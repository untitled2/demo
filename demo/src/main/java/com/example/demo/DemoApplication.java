package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication
{
    public static final String FILEPATH = "classpath:static/status";
    public static final String PACKAGE = "Package: ";
    public static final String DEPENDS = "Depends: ";
    public static final String DESCR = "Description: ";

    public static void main(String[] args)
    {
        System.setProperty("server.servlet.context-path", "/api/packages");
		SpringApplication.run(DemoApplication.class, args);
	}
}
