package program.util;

import program.lang.en_US;
import program.lang.lipsum;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageHandler {
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, String> languages = new HashMap<>();

    private final en_US en_us = new en_US();
    private final lipsum lipsum = new lipsum();


    public LanguageHandler() {
        languages.put("en_US", en_us.LanguageName);
        languages.put("lipsum", lipsum.LanguageName); // Only temporary, For demonstration purposes. TODO Remove
    }

    public Map<String, String> getLanguageNames() {
        return languages;
    }

    public boolean setLanguage(String name) {
        log.info("Attempting to set language to " + name);
        if (languages.containsKey(name)) {
            switch (name) {
                case "en_US":
                    en_us.setAllStrings();
                    return true;
                case "lipsum":
                    lipsum.setAllStrings();
                    return true;
            }
        }
        return false;
    }
}
