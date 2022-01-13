import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JsonCollectionTest {
    JsonCollection jsonCollection = new JsonCollection("TEST");


    @Test
    public void testInsertWithoutIndexing() {
        insertSomeData();
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"356a192b7913b04c54574d18c28d46e6395428ab\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"da4b9237bacccdf19c0760cab7aec4a8359010b0\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));


        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testDeletionWithoutIndexing() {
        insertSomeData();
        jsonCollection.deleteUsingID("356a192b7913b04c54574d18c28d46e6395428ab");
        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"da4b9237bacccdf19c0760cab7aec4a8359010b0\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput,actualOutput);

    }


    @Test
    public void testInsertWithIndexing() throws NoSuchFieldException {
        insertSomeData();
        jsonCollection.makeIndexed("name");


        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"356a192b7913b04c54574d18c28d46e6395428ab\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}"  ;
        String actualOutput = jsonCollection.getCertainFromIndexed("Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput,actualOutput);

    }
    @Test
    void testDeletionWithIndexing() throws NoSuchFieldException {
        insertSomeData();
        jsonCollection.makeIndexed("name");
        jsonCollection.deleteUsingID("356a192b7913b04c54574d18c28d46e6395428ab");

        String expectedOutput = "{\"_id\":\"b6589fc6ab0dc82cf12099d1c2d40ab994e8410c\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}" ;
        String actualOutput = jsonCollection.getCertainFromIndexed("Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput,actualOutput);


    }



    public void insertSomeData() {

        String hasan = "{\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String khaled = "{\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        jsonCollection.insert(hasan);
        jsonCollection.insert(hasan);
        jsonCollection.insert(khaled);
    }


}