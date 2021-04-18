package pl.edu.agh.smart_repo.request_handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.smart_repo.common.results.Result;
import pl.edu.agh.smart_repo.services.file.FileService;
import pl.edu.agh.smart_repo.services.upload.FileUploadService;
import pl.edu.agh.smart_repo.services.search.SearchService;

import java.io.IOException;
import java.util.List;

@RestController
public class RequestController {

    @Autowired
    SearchService searchService;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    FileService fileService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile file) throws IOException {

        //TODO file extension could be checked here, change fileService to accept MultiparFile

        Result result = fileUploadService.processFile(file);

        if (result.isSuccess())
            return new ResponseEntity<>("added file: " + file.getOriginalFilename(), HttpStatus.OK);
        else
            return new ResponseEntity<>("error while adding file: " + file.getOriginalFilename() +
                    " error: '" + result.getMessage() + "'",
                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/search/{phrase}")
    @ResponseBody
    public ResponseEntity<List<String>> searchForPhrase(@PathVariable String phrase) {
        System.out.println("SEARCH for: " + phrase);
        List<String> documentsContainingPhraseNames = searchService.searchDocuments(phrase);
        return new ResponseEntity<>(documentsContainingPhraseNames, HttpStatus.OK);
    }
}
