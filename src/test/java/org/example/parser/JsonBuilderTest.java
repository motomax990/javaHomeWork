package org.example.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.model.Launch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonBuilderTest {

    private JsonBuilder builder;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        builder = new JsonBuilder();
    }

    private String loadResource(String name) throws IOException {
        return Files.readString(Path.of("src/test/resources/" + name));
    }

    @Test
    void toJsonSerializesSingleLaunch() throws IOException {
        String json = loadResource("single_launch.json");
        Launch launch = gson.fromJson(json, Launch.class);

        String result = builder.toJson(launch);
        assertNotNull(result);
        assertTrue(result.contains("FalconSat"));
        assertTrue(result.contains("5eb87cd9ffd86e000604b32a"));
    }

    @Test
    void toJsonSerializesLaunchList() throws IOException {
        String json = loadResource("launches_array.json");
        List<Launch> launches = gson.fromJson(json,
                new com.google.gson.reflect.TypeToken<List<Launch>>() {}.getType());

        String result = builder.toJson(launches);
        assertNotNull(result);
        assertTrue(result.contains("FalconSat"));
        assertTrue(result.contains("DemoSat"));
    }

    @Test
    void toJsonSerializesObjectWithNullFields() throws IOException {
        String json = loadResource("launch_null_fields.json");
        Launch launch = gson.fromJson(json, Launch.class);

        String result = builder.toJson(launch);
        assertNotNull(result);
        assertTrue(result.contains("Test Mission"));
    }

    @Test
    void buildDateQueryContainsCorrectDates() {
        String result = builder.buildDateQuery("2020-01-01", "2020-12-31");
        JsonObject parsed = gson.fromJson(result, JsonObject.class);

        assertTrue(parsed.has("query"));
        JsonObject query = parsed.getAsJsonObject("query");
        assertTrue(query.has("date_utc"));
        JsonObject dateFilter = query.getAsJsonObject("date_utc");
        assertEquals("2020-01-01T00:00:00.000Z", dateFilter.get("$gte").getAsString());
        assertEquals("2020-12-31T23:59:59.999Z", dateFilter.get("$lte").getAsString());
    }

    @Test
    void buildDateQueryContainsOptions() {
        String result = builder.buildDateQuery("2020-01-01", "2020-12-31");
        JsonObject parsed = gson.fromJson(result, JsonObject.class);

        assertTrue(parsed.has("options"));
        assertEquals(1000, parsed.getAsJsonObject("options").get("limit").getAsInt());
    }

    @Test
    void buildSuccessQueryTrueContainsCorrectFilter() {
        String result = builder.buildSuccessQuery(true);
        JsonObject parsed = gson.fromJson(result, JsonObject.class);

        assertTrue(parsed.has("query"));
        assertTrue(parsed.getAsJsonObject("query").get("success").getAsBoolean());
    }

    @Test
    void buildSuccessQueryFalseContainsCorrectFilter() {
        String result = builder.buildSuccessQuery(false);
        JsonObject parsed = gson.fromJson(result, JsonObject.class);

        assertFalse(parsed.getAsJsonObject("query").get("success").getAsBoolean());
    }

    @Test
    void buildSuccessQueryContainsOptions() {
        String result = builder.buildSuccessQuery(true);
        JsonObject parsed = gson.fromJson(result, JsonObject.class);

        assertTrue(parsed.has("options"));
        assertEquals(1000, parsed.getAsJsonObject("options").get("limit").getAsInt());
    }
}
