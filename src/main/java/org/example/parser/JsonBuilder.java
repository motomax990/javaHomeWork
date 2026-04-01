package org.example.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private final Gson gson = new Gson();

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public String buildDateQuery(String from, String to) {
        JsonObject dateFilter = new JsonObject();
        dateFilter.addProperty("$gte", from + "T00:00:00.000Z");
        dateFilter.addProperty("$lte", to + "T23:59:59.999Z");

        JsonObject query = new JsonObject();
        query.add("date_utc", dateFilter);

        JsonObject options = new JsonObject();
        options.addProperty("limit", 1000);

        JsonObject body = new JsonObject();
        body.add("query", query);
        body.add("options", options);

        return gson.toJson(body);
    }

    public String buildSuccessQuery(boolean success) {
        JsonObject query = new JsonObject();
        query.addProperty("success", success);

        JsonObject options = new JsonObject();
        options.addProperty("limit", 1000);

        JsonObject body = new JsonObject();
        body.add("query", query);
        body.add("options", options);

        return gson.toJson(body);
    }
}
