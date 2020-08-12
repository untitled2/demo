package com.example.demo.controller;

public class PackageNotFoundException extends RuntimeException
{
    public PackageNotFoundException(String name)
    {
        super("Package " + name + " could not be found.\n");
    }
}
