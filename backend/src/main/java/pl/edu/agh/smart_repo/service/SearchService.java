package pl.edu.agh.smart_repo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.smart_repo.common.document_fields.DocumentFields;
import pl.edu.agh.smart_repo.indexer.IndexerService;
import pl.edu.agh.smart_repo.indexer.TextCannotBeIndexedException;
import pl.edu.agh.smart_repo.translation.Language;
import pl.edu.agh.smart_repo.translation.TextCannotBeTranslatedException;
import pl.edu.agh.smart_repo.translation.TranslationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static pl.edu.agh.smart_repo.translation.Language.*;

@Service
@Slf4j
public class SearchService {

    @Autowired
    IndexerService indexerService;

    @Autowired
    TranslationService translationService;

    private static final List<Language> defaultLanguagesToSearch
            = new ArrayList<>(Arrays.asList(POLISH, FRENCH, SPANISH, GERMAN, ITALIAN));

    public List<String> searchDocuments(String phrase) {
        return searchDocuments(phrase, defaultLanguagesToSearch);
    }

    public List<String> searchDocuments(String phrase, List<Language> languagesToSearch) {
        log.info("Searching for: " + phrase + " in languages: " + languagesToSearch.toString() + "...");

        try {
            List<String> translatedPhrases = translationService.translate(phrase, Language.ENGLISH, languagesToSearch);
            return translatedPhrases.stream()
                    .flatMap(phraseInSomeLanguage -> indexerService.search(DocumentFields.CONTENTS, phraseInSomeLanguage).stream())
                    .collect(Collectors.toList());
        } catch (TextCannotBeTranslatedException | TextCannotBeIndexedException e) {
            log.error(e.getMessage());
            return Collections.singletonList(e.getMessage());
        }
    }
}
