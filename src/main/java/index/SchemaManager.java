package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface SchemaManager {
    void deleteCollection(String name);

    DatabaseSchema selectCollection(String name);

    boolean addCollection(DatabaseSchema collection);

    ArrayList<String> showCollectionNames();

    void dumpCollection(String collectionName) throws IOException;

    void commit() throws IOException;

    void importCollection(String collectionName, File location) throws IOException, ClassNotFoundException;

    List<DatabaseSchema> getJsonCollections();

    void setJsonCollections(List<DatabaseSchema> jsonCollections);
}
