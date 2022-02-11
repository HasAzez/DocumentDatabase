import Indexing.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JsonCollectionTest  {
    JsonCollection jsonCollection = new JsonCollection("TEST", "Example.json");

    @BeforeEach
    public void setup() throws JsonProcessingException {
        String hasan = "{\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String khaled = "{\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        jsonCollection.insert(hasan);
        jsonCollection.insert(hasan);
        jsonCollection.insert(khaled);
    }

    @Test
    public void testInsertWithoutIndexing() {
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"356a192b7913b04c54574d18c28d46e6395428ab\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"da4b9237bacccdf19c0760cab7aec4a8359010b0\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));


        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testDeletionWithoutIndexing() {
        jsonCollection.delete("_id","356a192b7913b04c54574d18c28d46e6395428ab");
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"da4b9237bacccdf19c0760cab7aec4a8359010b0\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));

        assertEquals(expectedOutput, actualOutput);

    }


    @Test
    public void testInsertWithIndexing() {
        jsonCollection.makeIndexOn("name",jsonCollection.getAll());
        jsonCollection.makeIndexOn("age",jsonCollection.getAll());
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"356a192b7913b04c54574d18c28d46e6395428ab\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("name","Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, actualOutput);

    }


    @Test
    public void testIDDeletionWithIndexing()  {
        jsonCollection.makeIndexOn("name",jsonCollection.getAll());
        jsonCollection.delete("_id","356a192b7913b04c54574d18c28d46e6395428ab");

        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("name","Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, actualOutput);


    }
    @Test
    public void testDeletionWithIndexing()  {
        jsonCollection.makeIndexOn("name",jsonCollection.getAll());
        jsonCollection.makeIndexOn("age",jsonCollection.getAll());
        jsonCollection.delete("name","Hasan");

       String actualOutput = jsonCollection.get("name","Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(actualOutput,"");

    }
    @Test
    public void  getDocument(){
        String id = "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c";
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("_id",id).get(0).toString();
        assertEquals(expectedOutput,actualOutput);
    }
        @Test
    public void getDocumentsBasedOnPropertyWithoutIndex(){
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"356a192b7913b04c54574d18c28d46e6395428ab\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("age","18").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, actualOutput);
    }


}