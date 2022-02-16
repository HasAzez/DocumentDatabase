package patterns;

import com.fasterxml.jackson.databind.JsonNode;
import json.utils.Instruction;

import java.util.List;

public interface ReadingPrivileges {

  List<JsonNode> find(Instruction instruction);

  List<JsonNode> findAll(String collectionName);
  List<String> showCollections();

}
