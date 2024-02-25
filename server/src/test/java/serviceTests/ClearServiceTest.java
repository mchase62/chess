package serviceTests;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
public class ClearServiceTest {
    static final ClearService service = new ClearService();

    @Test
    void clear() throws DataAccessException {
        service.clearData();
    }

//    @Test
//    void clear() throws DataAccessException {
//        service.clearData();
//        assertEquals(0, service.)
//    }
}
