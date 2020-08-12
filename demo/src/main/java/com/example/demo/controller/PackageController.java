package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.model.Package;
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
        final int OFFSET = 2;
        List<String> packages = new ArrayList<>();

        try (InputStream input = new FileInputStream(ResourceUtils.getFile(DemoApplication.FILEPATH));
             BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
        {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(DemoApplication.PACKAGE)) {
                    packages.add(line.substring(line.indexOf(":") + OFFSET));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(packages);

        return packages;
    }

    @GetMapping(":{name}")
    public Package info(@PathVariable String name)
    {
        final int OFFSET = 2;
        Package p = null;

        try (InputStream input = new FileInputStream(ResourceUtils.getFile(DemoApplication.FILEPATH));
             BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
        {
            String line;
            String currPkgName = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(DemoApplication.PACKAGE))
                    currPkgName = line.substring(line.indexOf(":") + OFFSET);

                if (line.equals(DemoApplication.PACKAGE + name)) {
                    p = new Package(name);

                    do {
                        line = reader.readLine();

                        if (line == null)
                            break;

                        if (line.startsWith(DemoApplication.DEPENDS)) {
                            String[] deps = line.substring(line.indexOf(":") + OFFSET).strip().split(",");

                            for (String d : deps) {
                                d = d.strip();
                                p.getDependencies().add(d.contains(" ") ? d.substring(0, d.indexOf(" ")) : d);
                            }

                        } else if (line.startsWith(DemoApplication.DESCR)) {
                            StringBuilder sb = new StringBuilder(line.substring(line.indexOf(":") + OFFSET));

                            while ((line = reader.readLine()).startsWith(" ")) {
                                sb.append(line);
                            }

                            p.setDescription(sb.toString());
                        }
                    } while (!line.startsWith(DemoApplication.PACKAGE));

                } else if (line.startsWith(DemoApplication.DEPENDS) && line.contains(name)) {
                    if (p != null)
                        p.getDependents().add(currPkgName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // package was not found
        if (p == null)
            throw new PackageNotFoundException(name);

        // success
        return p;
    }
}
