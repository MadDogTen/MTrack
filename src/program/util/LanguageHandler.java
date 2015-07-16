package program.util;

import program.lang.Language;
import program.lang.en_US;
import program.lang.lipsum;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageHandler {
    // This file is a temporary solution, and will be changing.
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, Language> languages = new HashMap<>();

    public LanguageHandler() {
        languages.put("en_US", new en_US());
        languages.put("lipsum", new lipsum()); // Only temporary, For demonstration purposes. TODO Remove
    }

    public Map<String, String> getLanguageNames() {
        Map<String, String> languageNames = new HashMap<>();
        languages.forEach((internalName, languages) -> {
            languageNames.put(internalName, languages.getName());
        });
        return languageNames;
    }

    public boolean setLanguage(String name) {
        log.info("Attempting to set language to " + name);
        if (languages.containsKey(name)) {
            languages.get(name).setAllStrings();
            return true;
        }
        return false;
    }
}
