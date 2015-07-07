package program.util;

import program.lang.en_US;
import program.lang.lipsum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class LanguageHandler {
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final HashMap<String, String> languages = new HashMap<>();

    private final en_US en_us = new en_US();
    private final lipsum lipsum = new lipsum();


    public LanguageHandler() {
        languages.put("en_US", en_us.registerLanguage());
        languages.put("lipsum", lipsum.registerLanguage());
    }

    public ArrayList<String> getLanguageNames() {
        ArrayList<String> languageNames = new ArrayList<>();
        languages.forEach((language, languageName) -> languageNames.add(languageName));
        return languageNames;
    }

    public void setLanguage(String name) {
        log.info("Attempting to set language to " + name);
        if (name.matches(languages.get("en_US"))) en_us.setAllStrings();
        else if (name.matches(languages.get("lipsum"))) lipsum.setAllStrings();
    }
}
