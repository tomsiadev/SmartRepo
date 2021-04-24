package pl.edu.agh.smart_repo.services.index;

public class TextCannotBeIndexedException extends RuntimeException {

    public TextCannotBeIndexedException() {
        super("Text have not been handled correctly by indexer. Probably it has not been initialized properly in file system.");
    }
}
