package com.example.demo;

import com.example.demo.model.DebPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class DemoApplication
{
    public static final String FILEPATH = "classpath:static/status";
    public static final String PACKAGE = "Package: ";
    public static final String DEPENDS = "Depends: ";
    public static final String DESCR = "Description: ";
    public static final int OFFSET = 2;

    // if the file never changes, we can use caches
    public static final List<String> allPkgCache = Collections.synchronizedList(new LinkedList<>());
    public static final ConcurrentHashMap<String, DebPackage> cache = new ConcurrentHashMap<>();

    public static void main(String[] args)
    {
        System.setProperty("server.servlet.context-path", "/api/packages");
		SpringApplication.run(DemoApplication.class, args);
	}
}
