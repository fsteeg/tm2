package com.quui.tm2.agents;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * The gazetteer labels each word with the entry in its lexicon for the given
 * word. The lexicon is defined in files for each category, containing instances
 * of the category in each line.
 */
public class Gazetteer implements Agent<String, String> {
    private static final String ZIP = "gazetteer.zip";
    private List<Annotation<String>> result;
    private Map<String, Set<String>> map;
    
    public Gazetteer(String s) {
      this();
  }

    public Gazetteer() {
        try {
            this.map = mapping("lst");
            if (map.isEmpty()) {
                throw new IllegalStateException(
                        "Could not initialize the Gazetteers lexicon!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public List<Annotation<String>> process(List<Annotation<String>> tokens) {
        result = new ArrayList<Annotation<String>>();
        for (Annotation<String> word : tokens) {
            Set<String> set = map.get(word.getValue().toLowerCase());
            if (set != null) {
                for (String label : set) {
                    Annotation<String> annotation = ImmutableAnnotation
                            .getInstance(getClass(), label, word.getStart(),
                                    word.getEnd());
                    result.add(annotation);
                }
            }
        }
        return result;
    }

    private Map<String, Set<String>> mapping(String filePattern)
            throws IOException {
        Map<String, Set<String>> mapping = new HashMap<String, Set<String>>();

        InputStream stream = Gazetteer.class.getResourceAsStream(ZIP);
        if (stream == null) {
            throw new IllegalStateException(
                    "Could not load Gazetteer resources in "
                            + Gazetteer.class.getResource("."));
        }
        ZipInputStream zip = new ZipInputStream(stream);
        ZipEntry e = null;
        while ((e = zip.getNextEntry()) != null) {
            if (e.getName().endsWith(filePattern)) {
                String entryName = e.getName();
                int n;
                File newFile = new File(entryName);
                String directory = newFile.getParent();
                if (directory == null) {
                    if (newFile.isDirectory())
                        break;
                }
                StringWriter writer = new StringWriter();
                byte[] buf = new byte[1024];
                while ((n = zip.read(buf, 0, 1024)) > -1)
                    writer.write(conv(buf), 0, n);
                writer.close();
                zip.closeEntry();
                Scanner s = new Scanner(writer.toString());
                while (s.hasNextLine()) {
                    String nextLine = s.nextLine();
                    Set<String> cats = mapping.get(nextLine);
                    // The category is the filename:
                    String cat = e.getName().substring(
                            e.getName().lastIndexOf('/') + 1).split("\\.")[0];
                    if (cats == null) {
                        cats = new HashSet<String>(Arrays.asList(cat));
                    } else cats.add(cat);
                    mapping.put(nextLine, cats);
                }
            }
        }
        return mapping;
    }

    private char[] conv(byte[] buf) {
        char[] res = new char[buf.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = (char) buf[i];
        }
        return res;
    }
    
    @Override
    public String toString() {
        return "Gazetteer using " + ZIP;
    }   

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Agent#configuration()
     */
    @SuppressWarnings("serial")
    public Map<?, ?> configuration() {
        return new HashMap<String, String>() {
            {
                put("gazetteer", ZIP);
            }
        };
    }
}
