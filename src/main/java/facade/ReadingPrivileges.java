package facade;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ReadingPrivileges {

  List<JsonNode> find(String property, String searched);

  List<JsonNode> findAll();
   String selectCollection(String collectionName);
  List<String> showCollections();

}
