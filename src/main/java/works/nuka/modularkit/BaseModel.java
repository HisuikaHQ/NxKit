package works.nuka.modularkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseModel {
    protected String deserialize() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    protected BaseModel serialize(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, this.getClass());
    }
}
