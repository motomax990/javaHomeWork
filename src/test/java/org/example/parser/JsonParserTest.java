package org.example.parser;

import com.google.gson.JsonSyntaxException;
import org.example.model.Launch;
import org.example.model.QueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    private JsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new JsonParser();
    }

    private String loadResource(String name) throws IOException {
        return Files.readString(Path.of("src/test/resources/" + name));
    }

    @Test
    void parseLaunchDeserializesSingleObject() throws IOException {
        String json = loadResource("single_launch.json");
        Launch launch = parser.parseLaunch(json);

        assertEquals("5eb87cd9ffd86e000604b32a", launch.getId());
        assertEquals("FalconSat", launch.getName());
        assertEquals(1, launch.getFlightNumber());
        assertEquals("2006-03-24T22:30:00.000Z", launch.getDateUtc());
        assertFalse(launch.getSuccess());
        assertFalse(launch.isUpcoming());
        assertEquals("Engine failure at 33 seconds and target altitude was not reached.", launch.getDetails());
    }

    @Test
    void parseLaunchesDeserializesArray() throws IOException {
        String json = loadResource("launches_array.json");
        List<Launch> launches = parser.parseLaunches(json);

        assertEquals(2, launches.size());
        assertEquals("FalconSat", launches.get(0).getName());
        assertEquals("DemoSat", launches.get(1).getName());
        assertEquals(1, launches.get(0).getFlightNumber());
        assertEquals(2, launches.get(1).getFlightNumber());
    }

    @Test
    void parseLaunchHandlesNullFields() throws IOException {
        String json = loadResource("launch_null_fields.json");
        Launch launch = parser.parseLaunch(json);

        assertEquals("abc123", launch.getId());
        assertEquals("Test Mission", launch.getName());
        assertNull(launch.getSuccess());
        assertNull(launch.getDetails());
        assertTrue(launch.isUpcoming());
    }

    @Test
    void parseLaunchDeserializesFailures() throws IOException {
        String json = loadResource("single_launch.json");
        Launch launch = parser.parseLaunch(json);

        assertNotNull(launch.getFailures());
        assertEquals(1, launch.getFailures().size());
        assertEquals(33, launch.getFailures().get(0).getTime());
        assertNull(launch.getFailures().get(0).getAltitude());
        assertEquals("merlin engine  failure", launch.getFailures().get(0).getReason());
    }

    @Test
    void parseLaunchDeserializesCores() throws IOException {
        String json = loadResource("single_launch.json");
        Launch launch = parser.parseLaunch(json);

        assertNotNull(launch.getCores());
        assertEquals(1, launch.getCores().size());
        assertEquals("5e9e289df35918033d3b2623", launch.getCores().get(0).getCore());
        assertEquals(1, launch.getCores().get(0).getFlight());
        assertFalse(launch.getCores().get(0).getReused());
        assertFalse(launch.getCores().get(0).getLandingAttempt());
        assertNull(launch.getCores().get(0).getLandingSuccess());
    }

    @Test
    void parseLaunchesSerializedNameMapsCorrectly() throws IOException {
        String json = loadResource("single_launch.json");
        Launch launch = parser.parseLaunch(json);

        assertEquals(1, launch.getFlightNumber());
        assertEquals("2006-03-24T22:30:00.000Z", launch.getDateUtc());
    }

    @Test
    void parseLaunchesReturnsEmptyListForEmptyArray() throws IOException {
        String json = loadResource("empty_array.json");
        List<Launch> launches = parser.parseLaunches(json);

        assertNotNull(launches);
        assertTrue(launches.isEmpty());
    }

    @Test
    void parseLaunchesThrowsExceptionForInvalidJson() throws IOException {
        String json = loadResource("invalid.json");
        assertThrows(JsonSyntaxException.class, () -> parser.parseLaunches(json));
    }

    @Test
    void parseLaunchThrowsExceptionForInvalidJson() throws IOException {
        String json = loadResource("invalid.json");
        assertThrows(JsonSyntaxException.class, () -> parser.parseLaunch(json));
    }

    @Test
    void parseQueryResponseDeserializesCorrectly() throws IOException {
        String json = loadResource("query_response.json");
        QueryResponse response = parser.parseQueryResponse(json);

        assertNotNull(response.getDocs());
        assertEquals(1, response.getDocs().size());
        assertEquals("FalconSat", response.getDocs().get(0).getName());
    }

    @Test
    void parseLaunchDeserializesFailureWithAltitude() throws IOException {
        String json = loadResource("launches_array.json");
        List<Launch> launches = parser.parseLaunches(json);

        Launch demoSat = launches.get(1);
        assertNotNull(demoSat.getFailures());
        assertEquals(1, demoSat.getFailures().size());
        assertEquals(289, demoSat.getFailures().get(0).getAltitude());
        assertEquals(301, demoSat.getFailures().get(0).getTime());
    }
}
