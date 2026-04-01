package org.example.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.example.model.Launch;
import org.example.model.QueryResponse;

import java.lang.reflect.Type;
import java.util.List;

public class JsonParser {

    private final Gson gson = new Gson();

    public List<Launch> parseLaunches(String json) {
        Type listType = new TypeToken<List<Launch>>() {}.getType();
        List<Launch> launches = gson.fromJson(json, listType);
        if (launches == null) {
            throw new JsonSyntaxException("Некорректный JSON: результат null");
        }
        return launches;
    }

    public Launch parseLaunch(String json) {
        Launch launch = gson.fromJson(json, Launch.class);
        if (launch == null) {
            throw new JsonSyntaxException("Некорректный JSON: результат null");
        }
        return launch;
    }

    public QueryResponse parseQueryResponse(String json) {
        QueryResponse response = gson.fromJson(json, QueryResponse.class);
        if (response == null) {
            throw new JsonSyntaxException("Некорректный JSON: результат null");
        }
        return response;
    }
}
