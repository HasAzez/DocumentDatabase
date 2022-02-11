import Indexing.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JsonCollectionTest {
    JsonCollection jsonCollection = new JsonCollection("TEST", "Example.json");

    @BeforeEach
    public void setup() throws JsonProcessingException {
        String hasan = "{\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String khaled = "{\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        String mahmoud = "{\"name\":\"Mahmoud\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        jsonCollection.insert(hasan);
        jsonCollection.insert(khaled);
        jsonCollection.insert(mahmoud);
    }
    @Test
    public void testDuplicate() throws JsonProcessingException {
        jsonCollection.insert("{\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}");
jsonCollection.getAll().stream().forEach(s->System.out.println(s.toPrettyString()));

    }

    @Test
    public void testInsertWithoutIndexing() {
        String expectedOutput = "{\"_id\":\"0c0fec5e4e216ff035579a8380e68f536a2072c8\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"fb7dda0435f69d23114a65918acab74d4743561a\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}\n" +
                "{\"_id\":\"f2852330b63dc24286a754d2a91cee1199c2928c\",\"name\":\"Mahmoud\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));


        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testDeletionWithoutIndexing() {
        jsonCollection.delete("_id", "0c0fec5e4e216ff035579a8380e68f536a2072c8");
        String expectedOutput = "{\"_id\":\"fb7dda0435f69d23114a65918acab74d4743561a\",\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}\n" +
                "{\"_id\":\"f2852330b63dc24286a754d2a91cee1199c2928c\",\"name\":\"Mahmoud\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.getAll().stream().map(Object::toString).collect(Collectors.joining("\n"));

        assertEquals(expectedOutput, actualOutput);

    }


    @Test
    public void testInsertWithIndexing() {
        jsonCollection.makeIndexOn("name", jsonCollection.getAll());
        jsonCollection.makeIndexOn("age", jsonCollection.getAll());
        String expectedOutput = "{\"_id\":\"0c0fec5e4e216ff035579a8380e68f536a2072c8\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("name", "Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, actualOutput);

    }


    @Test
    public void testIDDeletionWithIndexing() {
        jsonCollection.makeIndexOn("name", jsonCollection.getAll());
        jsonCollection.delete("_id", "0c0fec5e4e216ff035579a8380e68f536a2072c8");

        String actualOutput = jsonCollection.get("name", "Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals("", actualOutput);


    }

    @Test
    public void testDeletionWithIndexing() {
        jsonCollection.makeIndexOn("name", jsonCollection.getAll());
        jsonCollection.makeIndexOn("age", jsonCollection.getAll());
        jsonCollection.delete("name", "Hasan");

        String actualOutput = jsonCollection.get("name", "Hasan").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(actualOutput, "");

    }

    @Test
    public void getDocument() {
        String id = "0c0fec5e4e216ff035579a8380e68f536a2072c8";
        String expectedOutput = "{\"_id\":\"0c0fec5e4e216ff035579a8380e68f536a2072c8\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("_id", id).get(0).toString();
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void getDocumentsBasedOnPropertyWithoutIndex() {
        String expectedOutput = "{\"_id\":\"0c0fec5e4e216ff035579a8380e68f536a2072c8\",\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}\n" +
                "{\"_id\":\"f2852330b63dc24286a754d2a91cee1199c2928c\",\"name\":\"Mahmoud\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String actualOutput = jsonCollection.get("age", "18").stream().map(Object::toString).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, actualOutput);
    }


}