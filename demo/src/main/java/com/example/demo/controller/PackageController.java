package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.model.DebPackage;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class PackageController
{
    @GetMapping("/")
    public List<String> all()
    {
        if (!DemoApplication.allPkgCache.isEmpty())
            return DemoApplication.allPkgCache;

        synchronized(this) {
            if (!DemoApplication.allPkgCache.isEmpty())
                return DemoApplication.allPkgCache;

            // temporary list for adding packages
            List<String> tmp = new ArrayList<>();

            try (InputStream input = new FileInputStream(ResourceUtils.getFile(DemoApplication.FILEPATH));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(DemoApplication.PACKAGE)) {
                        tmp.add(line.substring(line.indexOf(":") + DemoApplication.OFFSET));
                    }
                }

                // synchronized addAll
                Collections.sort(tmp);
                DemoApplication.allPkgCache.addAll(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return DemoApplication.allPkgCache;
    }

    @GetMapping(":{name}")
    public DebPackage info(@PathVariable String name)
    {
        // block here to search for the package
        DebPackage p = DemoApplication.cache.computeIfAbsent(name, this::findDebPackage);

        // package was not found
        if (p == null)
            throw new PackageNotFoundException(name);

        return p;
    }

    private DebPackage findDebPackage(String name)
    {
        DebPackage p = null;

        try (InputStream input = new FileInputStream(ResourceUtils.getFile(DemoApplication.FILEPATH));
             BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
        {
            String line;
            String currPkgName = "";
            List<String> dependents = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(DemoApplication.PACKAGE))
                    currPkgName = line.substring(line.indexOf(":") + DemoApplication.OFFSET);

                if (line.equals(DemoApplication.PACKAGE + name)) {
                    p = new DebPackage(name);

                    do {
                        line = reader.readLine();

                        if (line == null)
                            break;

                        if (line.startsWith(DemoApplication.DEPENDS)) {
                            String[] deps = line.substring(line.indexOf(":") + DemoApplication.OFFSET).strip().split(",");

                            for (String d : deps) {
                                d = d.strip();

                                // pipe symbol means OR
                                if (d.contains("|")) {
                                    String tmp = "";
                                    String[] orDeps = d.split("\\|");
                                    for (int i = 0; i < orDeps.length; i++) {
                                        d = orDeps[i].strip();
                                        tmp += d.contains(" ") ? d.substring(0, d.indexOf(" ")) : d;
                                        if (i < orDeps.length-1)
                                            tmp += " | ";
                                    }
                                    p.getDependencies().add(tmp);
                                } else {
                                    p.getDependencies().add(d.contains(" ") ? d.substring(0, d.indexOf(" ")) : d);
                                }
                            }

                        } else if (line.startsWith(DemoApplication.DESCR)) {
                            StringBuilder sb = new StringBuilder(line.substring(line.indexOf(":") + DemoApplication.OFFSET));

                            while ((line = reader.readLine()).startsWith(" ")) {
                                sb.append(line);
                            }

                            p.setDescription(sb.toString());
                        }
                    } while (!line.startsWith(DemoApplication.PACKAGE));

                } else if (line.startsWith(DemoApplication.DEPENDS) && (line.contains(" " + name + " ") || line.contains(" " + name + ",") || line.contains(", " + name) || line.contains(": " + name))) {
                    dependents.add(currPkgName);
                }
            }

            // add dependents collected from file
            if (p != null)
                p.getDependents().addAll(dependents);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }
}
