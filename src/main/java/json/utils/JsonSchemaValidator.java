package json.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.stream.Collectors;

public class JsonSchemaValidator   {
    private final JsonSchema schemaReader;
    private final ObjectMapper jsonMapper;

    public JsonSchemaValidator(SpecVersion.VersionFlag version, InputStream inputStream) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(version);

        this.schemaReader = factory.getSchema(inputStream);
        this.jsonMapper = new ObjectMapper();
    }
    public JsonSchemaValidator(File file) throws FileNotFoundException {
        this(SpecVersion.VersionFlag.V7, new FileInputStream(file));
    }

    public JsonSchemaValidator(String fileName) throws FileNotFoundException {

        this(SpecVersion.VersionFlag.V7, new FileInputStream("Schema\\"+fileName));
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
