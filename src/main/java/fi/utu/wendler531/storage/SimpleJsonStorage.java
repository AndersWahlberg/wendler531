package fi.utu.wendler531.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class SimpleJsonStorage {

    private final Path file;
    private final Gson gson;

    public SimpleJsonStorage(Path file) {
        this.file = file;

        JsonSerializer<LocalDate> ser = (src, typeOfSrc, ctx) -> new JsonPrimitive(src.toString());
        JsonDeserializer<LocalDate> des = (json, typeOfT, ctx) -> LocalDate.parse(json.getAsString());

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, ser)
                .registerTypeAdapter(LocalDate.class, des)
                .create();
    }

    public <T> void save(T obj) throws Exception {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(obj, w);
        }
    }

    public <T> T load(Class<T> clazz) throws Exception {
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return gson.fromJson(r, clazz);
        }
    }
}