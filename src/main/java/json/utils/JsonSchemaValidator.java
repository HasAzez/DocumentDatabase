package json.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;

public class JsonSchemaValidator   {
    private final JsonSchema schemaReader;
    private final ObjectMapper jsonMapper;


    public JsonSchemaValidator(JsonNode jsonNode){
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

        this.schemaReader = factory.getSchema(jsonNode);
        this.jsonMapper = new ObjectMapper();

    }

    public JsonSchemaValidator(String fileName) throws FileNotFoundException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

        this.schemaReader = factory.getSchema( new FileInputStream("Schema\\"+fileName));
        this.jsonMapper = new ObjectMapper();
    }

    public boolean isValid(String jsonSentence) throws JsonProcessingException {
        JsonNode jsonDocument = jsonMapper.readTree(jsonSentence);
        return schemaReader.validate(jsonDocument)
                .size() == 0;
    }

    public String errorList(String jsonSentence) throws JsonProcessingException {
        JsonNode jsonDocument = jsonMapper.readTree(jsonSentence);
        return schemaReader.validate(jsonDocument)
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }
}
