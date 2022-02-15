import cache.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public  class FIFOCacheTest {
    Cache<Integer,Integer> cache;
    @BeforeEach
      void setup() {
         cache = new FIFOCache(3);
         cache.put(1,1);
         cache.put(2,2);
         cache.put(3,3);
    }

    @Test
    public void checkIfTheyAdded() {

        assertEquals(3, cache.size());
           }


    @Test
    public void removeTheEldestOneWhenAdding() {
      cache.put(4,4);
      assertFalse(cache.containsKey(1));
    }
    @Test
    public void addingAfterRemovingTheEldestOne() {
        cache.put(4,4);
        assertTrue(cache.containsKey(4));
    }
    @Test
    public void updatingValue() {
        cache.put(3,4);
        assertEquals(4,cache.get(3));
    }



}