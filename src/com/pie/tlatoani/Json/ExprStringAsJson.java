package com.pie.tlatoani.Json;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import com.pie.tlatoani.Json.API.Json;
import com.pie.tlatoani.Json.API.JsonObject;
import com.pie.tlatoani.Json.API.stream.JsonParsingException;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;


import org.json.simple.JSONObject;

import java.io.StringReader;

/**
 * Created by Tlatoani on 5/8/16.
 */
public class ExprStringAsJson extends SimpleExpression<JsonObject> {
    private Expression<String> stringExpression;

    public static void test() {
        org.json.simple.JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", "ONE");
        jsonObject.put("2", "TWO");
        jsonObject.put("3", "THREE");
        Mundo.debug(ExprStringAsJson.class, "JSON " + jsonObject);
    }

    @Override
    protected JsonObject[] get(Event event) {
        JsonObject jsonObject = null;
        try {
            jsonObject = Json.createReader(new StringReader(stringExpression.getSingle(event))).readObject();
        } catch (JsonParsingException e) {}
        return new JsonObject[]{jsonObject};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends JsonObject> getReturnType() {
        return JsonObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "json of string %string%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        stringExpression = (Expression<String>) expressions[0];
        return true;
    }
}
