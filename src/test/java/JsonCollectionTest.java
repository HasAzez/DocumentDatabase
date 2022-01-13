import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class JsonCollectionTest {
    JsonCollection jsonCollection = new JsonCollection("TEST");


    @Test
    public void testInsertWithoutIndexing() {
        insertSomeData();
        jsonCollection.getAll().stream().forEach(System.out::println);
    }

    @Test
    void testDeletionWithoutIndexing() {
        insertSomeData();
        jsonCollection.deleteUsingID("356a192b7913b04c54574d18c28d46e6395428ab");
        jsonCollection.getAll().stream().forEach(System.out::println);

    }



    @Test
    public void testInsertWithIndexing() throws NoSuchFieldException {
        insertSomeData();
        jsonCollection.makeIndexed("name");

        for (Iterator<JsonObject> it = jsonCollection.getCertainFromIndexed("Hasan"); it.hasNext(); ) {
            JsonObject jo = it.next();
            System.out.println(jo);
        }



    }
    @Test
    void testDeletionWithIndexing() throws NoSuchFieldException {
        insertSomeData();
        jsonCollection.makeIndexed("name");
        jsonCollection.deleteUsingID("356a192b7913b04c54574d18c28d46e6395428ab");

        for (Iterator<JsonObject> it = jsonCollection.getCertainFromIndexed("Hasan"); it.hasNext(); ) {
            JsonObject jo = it.next();
            System.out.println(jo);
        }

    }
    public void insertSomeData() {

        String hasan = "{\"name\":\"Hasan\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":18}";
        String khaled = "{\"name\":\"Khaled\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":20}";
        jsonCollection.insert(hasan);
        jsonCollection.insert(hasan);
        jsonCollection.insert(khaled);
    }


}