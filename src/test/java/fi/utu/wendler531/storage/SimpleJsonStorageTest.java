package fi.utu.wendler531.storage;

import fi.utu.wendler531.app.AppState;
import fi.utu.wendler531.app.LiftSettings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SimpleJsonStorage}.
 */
class SimpleJsonStorageTest {

    @Test
    void appStateRoundTripWorks() throws Exception {
        Path temp = Files.createTempFile("wendler531", ".json");
        SimpleJsonStorage storage = new SimpleJsonStorage(temp);

        AppState state = AppState.defaultState();
        state.getUserProfile().setName("Anders");
        state.getLiftSettings().setTmPercent(0.90);
        state.getLiftSettings().setOneRepMax(LiftSettings.MainLift.SQUAT, 200);

        storage.save(state);

        AppState loaded = storage.load(AppState.class);

        assertNotNull(loaded);
        assertEquals("Anders", loaded.getUserProfile().getName());
        assertEquals(0.90, loaded.getLiftSettings().getTmPercent(), 0.0001);
        assertEquals(200.0, loaded.getLiftSettings().getOneRepMax(LiftSettings.MainLift.SQUAT), 0.0001);
    }

    @Test
    void load_returnsNull_whenFileIsBlank() throws Exception {
        Path temp = Files.createTempFile("wendler531_blank", ".json");
        Files.writeString(temp, "   \n  \r\n", StandardCharsets.UTF_8);

        SimpleJsonStorage storage = new SimpleJsonStorage(temp);
        AppState loaded = storage.load(AppState.class);

        assertNull(loaded);
    }

    @Test
    void load_throwsIOException_whenJsonIsInvalid() throws Exception {
        Path temp = Files.createTempFile("wendler531_invalid", ".json");
        Files.writeString(temp, "{ this is not valid json", StandardCharsets.UTF_8);

        SimpleJsonStorage storage = new SimpleJsonStorage(temp);

        assertThrows(IOException.class, () -> storage.load(AppState.class));
    }
}