package pl.edu.agh.smart_repo.common.document_fields;

import lombok.Data;

@Data
public class DocumentStructure {

    private String name;
    private String path;
    private String contents;
    private String keywords;
    private String creationDate;
    private String modificationDate;
    private String language;
    private String size;
    private String extension;

    public void setByDocumentField(DocumentField documentField, String val) {
        switch (documentField) {
            case NAME:
                this.name = val;
                break;
            case PATH:
                this.path = val;
                break;
            case CONTENTS:
                this.contents = val;
                break;
            case KEYWORDS:
                this.keywords = val;
                break;
            case CREATION_DATE:
                this.creationDate = val;
                break;
            case MODIFICATION_DATE:
                this.modificationDate = val;
                break;
            case LANGUAGE:
                this.language = val;
                break;
            case SIZE:
                this.size = val;
                break;
            case EXTENSION:
                this.extension = val;
                break;
        }
    }

    public String getByDocumentField(DocumentField documentField) {
        switch (documentField) {
            case NAME:
                return this.name;
            case PATH:
                return this.path;
            case CONTENTS:
                return this.contents;
            case KEYWORDS:
                return this.keywords;
            case CREATION_DATE:
                return this.creationDate;
            case MODIFICATION_DATE:
                return this.modificationDate;
            case LANGUAGE:
                return this.language;
            case SIZE:
                return this.size;
            case EXTENSION:
                return this.extension;
            default:
                return null;
        }
    }
}