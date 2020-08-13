package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

public class DebPackage
{
    private String name;
    private String description = "";
    private Set<String> dependencies = new HashSet<>();
    private Set<String> dependents = new HashSet<>();

    public DebPackage() {}

    public DebPackage(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<String> getDependents() {
        return dependents;
    }

    public void setDependents(Set<String> dependents) {
        this.dependents = dependents;
    }
}
