import com.fasterxml.jackson.core.JsonProcessingException;
import json.utils.JsonSchemaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class JsonSchemaValidatorTest {
    JsonSchemaValidator validator;


    @BeforeEach
    void setup() throws FileNotFoundException {
        validator = new JsonSchemaValidator("Example.json");
    }


    @Test
    void isValid() throws JsonProcessingException {
        String khaled = "{\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";

        assertTrue(validator.isValid(khaled));
    }

    @Test
    void StringInsteadOfInteger() throws JsonProcessingException {
        String ahmad = "{\"name\":\"Ahmad\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":\"thirteen\"}";
        String expectedOutput = "$.age: string found, integer expected";
        assertEquals(expectedOutput, validator.errorList(ahmad));

    }

    @Test
    void missingOneField() throws JsonProcessingException {
        String ahmad = "{\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":13}";
        String expectedOutput = "$.name: is missing but it is required";
        assertEquals(expectedOutput, validator.errorList(ahmad));
    }
}