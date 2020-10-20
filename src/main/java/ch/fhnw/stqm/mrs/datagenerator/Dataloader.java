package ch.fhnw.stqm.mrs.datagenerator;

public interface Dataloader {
    /**
     * Load data into MRS database.
     * @param connection to use when writing to database.
     * @throws Exception whenever something goes wrong.
     */
    void load() throws Exception;
}
