import Indexing.JsonCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {
   static CollectionManager collectionManager;
   static JsonCollection studentsCollection;
    @BeforeAll
    static void setUp() throws IOException {
        collectionManager = CollectionManager.newInstance(new ArrayList<>());
        studentsCollection = new JsonCollection("Students");
        collectionManager.setCurrentCollection(studentsCollection);
        studentsCollection.insert("{\"name\":\"Hasan\",\"Subjects\":[\"English\",\"Math\",\"Physics\"],\"age\":12}");
        studentsCollection.insert("{\"name\":\"Mohammad\",\"subjects\":[\"English\",\"Math\",\"Gym\"],\"age\":13}");
        collectionManager.dumpCollection();

    }

    @Test
    void selectCollection() {
        JsonCollection   teachersCollection = new JsonCollection("Teachers");
        collectionManager.addCollection(teachersCollection);
        collectionManager.selectCollection("Teachers");
        assertEquals(collectionManager.getCurrentCollection(),teachersCollection);
    }
    @Test
    void checkIfInserted()  {
             assertEquals(collectionManager.getCurrentCollection().getCount(), 2);
    }
    @Test
    void checkIFDeleted(){
        collectionManager.deleteCollection("Students");
        assertFalse(collectionManager.selectCollection("Students"));
    }
    @Test
    void backup() throws IOException, ClassNotFoundException {
       collectionManager.deleteCollection("Students");
       collectionManager.importCollection(new File("Students.coll"));
       assertTrue(collectionManager.selectCollection("Students"));
    }



}