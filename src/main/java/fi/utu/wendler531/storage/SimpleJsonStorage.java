package fi.utu.wendler531.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Simple JSON-based storage helper for saving and loading application data.
 *
 * <p>This class uses Gson for serialization and deserialization and includes
 * custom handling for {@link LocalDate} values.</p>
 */
public class SimpleJsonStorage {

    private final Path file;
    private final Gson gson;

    /**
     * Creates a new storage instance for the given file path.
     *
     * @param file the target JSON file path
     * @throws IllegalArgumentException if the file path is null
     */
    public SimpleJsonStorage(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        this.file = file;

        JsonSerializer<LocalDate> ser = (src, typeOfSrc, ctx) -> new JsonPrimitive(src.toString());
        JsonDeserializer<LocalDate> des = (json, typeOfT, ctx) -> LocalDate.parse(json.getAsString());

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, ser)
                .registerTypeAdapter(LocalDate.class, des)
                .create();
    }

    /**
     * Serializes the given object and saves it to the configured JSON file.
     *
     * @param obj the object to save
     * @param <T> the object type
     * @throws IllegalArgumentException if the object is null
     * @throws IOException if writing the file fails
     */
    public <T> void save(T obj) throws IOException {
        if (obj == null) {
            throw new IllegalArgumentException("Object to save cannot be null");
        }

        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(obj, writer);
        }
    }

    /**
     * Loads JSON from the configured file and deserializes it into the given class type.
     *
     * <p>Returns {@code null} if the file does not exist, is empty, or contains only
     * whitespace. Gson may also return {@code null} if the JSON content itself is
     * the literal {@code null}.</p>
     *
     * @param clazz the target class to deserialize into
     * @param <T> the object type
     * @return the deserialized object, or {@code null} if no usable data exists
     * @throws IllegalArgumentException if the target class is null
     * @throws IOException if reading fails or the JSON content is invalid
     */
    public <T> T load(Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("Target class cannot be null");
        }

        if (!Files.exists(file)) {
            return null;
        }
        if (Files.size(file) == 0) {
            return null;
        }

        // Also handle files that contain only whitespace or line breaks.
        String content = Files.readString(file, StandardCharsets.UTF_8);
        if (content.trim().isEmpty()) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (JsonParseException e) {
            throw new IOException("Invalid JSON file: " + e.getMessage(), e);
        }
    }
}