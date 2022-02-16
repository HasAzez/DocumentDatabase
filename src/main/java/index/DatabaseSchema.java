package index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import json.utils.JsonSchemaValidator;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

public interface DatabaseSchema extends Serializable {
    void insert(String jsonSentence) throws JsonProcessingException;

    void delete(String propertyName, String key);

    List<JsonNode> getAll();

    void update(String id, String jsonSentence) throws JsonProcessingException;

    List<JsonNode> get(String propertyName, String searched);

    void makeIndexOn(String propertyName);

    String getName();

    JsonSchemaValidator getValidator() throws FileNotFoundException;
}
