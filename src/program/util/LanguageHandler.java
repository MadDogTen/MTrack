package program.util;

import program.lang.en_US;
import program.lang.lipsum;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageHandler {
    // This file is a temporary solution, and will be changing.
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, String> languages = new HashMap<>();

    public LanguageHandler() {
        languages.put("en_US", "English US");
        languages.put("lipsum", "Lipsum"); // Only temporary, For demonstration purposes. TODO Remove
    }

    public Map<String, String> getLanguageNames() {
        return languages;
    }

    public boolean setLanguage(String name) {
        log.info("Attempting to set language to " + name);
        if (languages.containsKey(name)) {
            switch (name) {
                case "en_US":
                    new en_US().setAllStrings();
                    return true;
                case "lipsum":
                    new lipsum().setAllStrings();
                    return true;
            }
        }
        return false;
    }
}
