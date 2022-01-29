package json.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonSchemaValidator {
    private final JsonSchemaFactory factory;
    private final InputStream inputStream;
    private final JsonSchema schemaReader;
    private final ObjectMapper jsonMapper;

    public JsonSchemaValidator(SpecVersion.VersionFlag version, String fileName) {
        this.factory = JsonSchemaFactory.getInstance(version);
        this.inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName);
        this.schemaReader = factory.getSchema(inputStream);
        this.jsonMapper = new ObjectMapper();
    }

    public JsonSchemaValidator(String fileName) {
        this(SpecVersion.VersionFlag.V7, fileName);
    }

    public boolean isValid(String jsonSentence) throws JsonProcessingException {
        JsonNode jsonDocument = jsonMapper.readTree(jsonSentence);
        return schemaReader.validate(jsonDocument).size() == 0;
    }

    public String errorList(String jsonSentence) throws JsonProcessingException {
        JsonNode jsonDocument = jsonMapper.readTree(jsonSentence);
        return schemaReader.validate(jsonDocument).stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
