package pl.edu.agh.smart_repo.services.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.smart_repo.common.document_fields.DocumentStructure;
import pl.edu.agh.smart_repo.common.json.EscapeCharMapper;
import pl.edu.agh.smart_repo.configuration.ConfigurationFactory;
import pl.edu.agh.smart_repo.common.results.Result;
import pl.edu.agh.smart_repo.common.results.ResultType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import pl.edu.agh.smart_repo.services.index.IndexerService;
import pl.edu.agh.smart_repo.services.parse.ParserService;

@Slf4j
@Service
public class FileUploadService {
    private final Path storagePath;
    private final EscapeCharMapper escapeCharMapper;

    @Autowired
    ParserService parserService;
    @Autowired
    IndexerService indexerService;

    @Autowired
    public FileUploadService(ConfigurationFactory configurationFactory) {
        storagePath = configurationFactory.getStoragePath();
        escapeCharMapper = new EscapeCharMapper();
    }

    public Result processFile(MultipartFile file, String path) {
        //TODO: this part should be retrieved from frontend
        String fileName = file.getOriginalFilename();
        log.info("Start processing file: " + fileName);

        Path filePath = Paths.get(storagePath.toString(), path, fileName);

        File new_file = new File(filePath.toUri());

        try (FileOutputStream fos = new FileOutputStream(new_file)) {
            fos.write(file.getBytes());
        } catch (FileNotFoundException e) {
            log.error("Error: file cannot be created.");
            return new Result(ResultType.FAILURE, e);
        } catch (IOException e) {
            log.error("Error while saving file.");
            return new Result(ResultType.FAILURE, e);
        }

        String parsed = parserService.parse(new_file, fileName);

        if (parsed == null) {
            return new Result(ResultType.FAILURE, "Failed to parse file.");
        }

        parsed = escapeCharMapper.mapAll(parsed).trim();

        DocumentStructure documentStructure = new DocumentStructure();

        //TODO retrieve remaining arguments from frontend`s request
        documentStructure.setName(fileName);
        documentStructure.setPath(filePath.toString());
        documentStructure.setContents(parsed);

        return indexerService.indexDocument(documentStructure);
    }
}
