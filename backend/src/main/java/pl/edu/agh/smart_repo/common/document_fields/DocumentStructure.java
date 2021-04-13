package pl.edu.agh.smart_repo.common.document_fields;

import lombok.Data;

@Data
public class DocumentStructure {

    private String path;
    private String contents;
    private String keywords;

    public void setByName(DocumentFields documentField, String val)
    {
        switch(documentField)
        {
            case PATH:
                this.path = val;
                break;
            case CONTENTS:
                this.contents = val;
                break;
            case KEYWORDS:
                this.keywords = val;
                break;
        }
    }

    public String getByName(DocumentFields documentField)
    {
        switch(documentField)
        {
            case PATH:
                return this.path;
            case CONTENTS:
                return this.contents;
            case KEYWORDS:
                return this.keywords;
            default:
                return null;
        }
    }
}
