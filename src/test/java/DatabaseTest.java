import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
   static Database database;

    @BeforeAll
    static void setUp() throws IOException {
        database = Database.getDb();
        database.setCurrentCollection(database.createCollection("Students"));
        database.insert("{\"name\":\"Hasan\",\"Subjects\":[\"English\",\"Math\",\"Physics\"],\"age\":12}");
        database.insert("{\"name\":\"Mohammad\",\"subjects\":[\"English\",\"Math\",\"Gym\"],\"age\":13}");
        database.dumpCollection();
        database.createCollection("Teachers");
    }

    @Test
    void selectCollection() {
        database.selectCollection("Teachers");
        assertEquals(database.getNameOfCurrentCollection(), "Teachers");
    }
    @Test
    void checkIfInserted() {

        assertEquals(database.getCountOfCurrentCollection(), 2);
    }
    @Test
    void checkIFDeleted(){
        database.deleteCollection("Students");
        assertFalse(database.selectCollection("Students"));
    }
    @Test
    void backup() throws IOException, ClassNotFoundException {
       database.deleteCollection("Students");
       database.importCollection(new File("Students.ndb"));
       assertTrue(database.selectCollection("Students"));
    }



}