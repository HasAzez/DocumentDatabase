package facade;

import java.io.File;
import java.io.IOException;

public interface WritingPrivileges {

  String createCollection(String collectionName, String jsonSchema) throws IOException;

  String deleteCollection() throws IOException;

  String add(String jsonString) throws IOException;

  String delete(String property, String value) throws IOException;

  String update(String property, String value, String jsonString) throws IOException;

  String selectCollection(String collectionName);

  String dumpCollection() throws IOException;

  String importCollection(File file) throws IOException, ClassNotFoundException;

  String makeIndexOn(String propertyName);
}
