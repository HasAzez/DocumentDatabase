import index.IndexBuilder;
import index.JsonCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import index.CollectionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {
    static CollectionManager collectionManager;
    static JsonCollection studentsCollection;
    static IndexBuilder indexBuilder=  new IndexBuilder.Builder(new ArrayList<>())
            .indexType(new HashMap<>())
            .indexesSelector(new HashMap<>())
            .build() ;
    @BeforeAll
    static void setUp() throws IOException {
        collectionManager = CollectionManager.INSTANCE;
        studentsCollection = new JsonCollection("Students", "Example.json", indexBuilder);
        collectionManager.addCollection(studentsCollection);
      collectionManager.selectCollection("Students");
        studentsCollection.insert("{\"name\":\"Hasan\",\"Subjects\":[\"English\",\"Math\",\"Physics\"],\"age\":12}");
        studentsCollection.insert("{\"name\":\"Mohammad\",\"subjects\":[\"English\",\"Math\",\"Gym\"],\"age\":13}");
        collectionManager.dumpCollection();

    }

    @Test
    void selectCollection() {
        JsonCollection teachersCollection = new JsonCollection("Teachers", "Example.json", indexBuilder);
        collectionManager.addCollection(teachersCollection);
        collectionManager.selectCollection("Teachers");
        assertEquals(collectionManager.getCurrentCollection(), teachersCollection);
    }

    @Test
    void checkIFDeleted() {
        collectionManager.deleteCollection("Students");
        collectionManager.selectCollection("Students");
        assertNull(collectionManager.getCurrentCollection());
    }

    @Test
    void backup() throws IOException, ClassNotFoundException {
        collectionManager.deleteCollection("Students");
        collectionManager.importCollection(new File("Students.coll"));
        collectionManager.selectCollection("Students");
        assertEquals("Students", collectionManager.getCurrentCollection().getName());
    }
    @Test
    void showCollectionNames() {
        collectionManager.deleteCollection("Teachers");
        JsonCollection teachersCollection = new JsonCollection("Teachers", "Example.json", indexBuilder);
        collectionManager.addCollection(teachersCollection);
        assertEquals("[Students, Teachers]",collectionManager.showCollectionNames().toString());
    }


}