package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.GameConfigOptions;
import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FromGameConfigOptionsTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsFullyPopulated() throws Exception {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.lang = "en";
        model.apiKey = "key_123";
        model.appPackageId = "com.example.app";
        model.deviceId = "dev_456";
        model.userId = "user_789";
        model.sdkVersion = "2.0.0";
        model.apiBaseUrl = "https://api.example.com";
        model.screenOrientation = "portrait";
        model.userAgent = "TestAgent";
        model.fullScreen = true;
        model.gameInstanceId = "game_inst_1";

        model.safeAreaInsets = new SafeAreaInsets();
        model.safeAreaInsets.top = 10;
        model.safeAreaInsets.bottom = 20;
        model.safeAreaInsets.left = 5;
        model.safeAreaInsets.right = 5;

        List<GameDataPlaceholder> placeholders = new ArrayList<>();
        GameDataPlaceholder ph1 = new GameDataPlaceholder();
        ph1.name = "ph_name";
        ph1.type = "text";
        ph1.value = "ph_value";
        placeholders.add(ph1);
        model.placeholders = placeholders;

        Map<String, Object> extraOptions = new HashMap<>();
        extraOptions.put("custom_key", "custom_value");
        model.userExtraOptions = extraOptions;

        FromGameConfigOptions parser = new FromGameConfigOptions(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);

        assertEquals("sess_1", json.getString(ModelFields.GameConfigOptions.sessionId));
        assertEquals("en", json.getString(ModelFields.GameConfigOptions.lang));
        assertEquals("key_123", json.getString(ModelFields.GameConfigOptions.apiKey));
        assertEquals("com.example.app", json.getString(ModelFields.GameConfigOptions.appPackageId));
        assertEquals("dev_456", json.getString(ModelFields.GameConfigOptions.deviceId));
        assertEquals("user_789", json.getString(ModelFields.GameConfigOptions.userId));
        assertEquals("2.0.0", json.getString(ModelFields.GameConfigOptions.sdkVersion));
        assertEquals("https://api.example.com", json.getString(ModelFields.GameConfigOptions.apiBaseUrl));
        assertEquals("portrait", json.getString(ModelFields.GameConfigOptions.screenOrientation));
        assertEquals("TestAgent", json.getString(ModelFields.GameConfigOptions.userAgent));
        assertTrue(json.getBoolean(ModelFields.GameConfigOptions.fullScreen));
        assertEquals("game_inst_1", json.getString(ModelFields.GameConfigOptions.gameInstanceId));

        JSONObject safeAreaJson = json.getJSONObject(ModelFields.GameConfigOptions.safeAreaInsets);
        assertEquals(10, safeAreaJson.getInt(ModelFields.SafeAreaInsets.top));
        assertEquals(20, safeAreaJson.getInt(ModelFields.SafeAreaInsets.bottom));
        assertEquals(5, safeAreaJson.getInt(ModelFields.SafeAreaInsets.left));
        assertEquals(5, safeAreaJson.getInt(ModelFields.SafeAreaInsets.right));

        JSONArray placeholdersJson = json.getJSONArray(ModelFields.GameConfigOptions.placeholders);
        assertEquals(1, placeholdersJson.length());
        assertEquals("ph_name", placeholdersJson.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.name));
        assertEquals("text", placeholdersJson.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.type));
        assertEquals("ph_value", placeholdersJson.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.value));

        JSONObject extraOptionsJson = json.getJSONObject(ModelFields.GameConfigOptions.userExtraOptions);
        assertEquals("custom_value", extraOptionsJson.getString("custom_key"));
    }

    // --- Негативные и граничные сценарии ---

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromGameConfigOptions parser = new FromGameConfigOptions(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenSafeAreaInsetsIsNull() {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.safeAreaInsets = null; // Вызовет NPE при new FromSafeAreaInsets(null)
        model.userExtraOptions = new HashMap<>();

        FromGameConfigOptions parser = new FromGameConfigOptions(model);

        // Базовый класс ModelToStringParser перехватит NPE и вернет null
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesNullPlaceholdersGracefully() throws Exception {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.placeholders = null;
        model.safeAreaInsets = new SafeAreaInsets();
        model.userExtraOptions = new HashMap<>();

        FromGameConfigOptions parser = new FromGameConfigOptions(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("sess_1", json.getString(ModelFields.GameConfigOptions.sessionId));
        assertFalse(json.has(ModelFields.GameConfigOptions.placeholders));
    }

    @Test
    public void parse_handlesNullUserExtraOptionsGracefully() throws Exception {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.safeAreaInsets = new SafeAreaInsets();
        model.userExtraOptions = null; // Теперь это безопасно благодаря проверке if (model.userExtraOptions != null)

        FromGameConfigOptions parser = new FromGameConfigOptions(model);
        String result = parser.parse();

        // Раньше здесь был бы assertNull(result), теперь парсинг проходит успешно
        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("sess_1", json.getString(ModelFields.GameConfigOptions.sessionId));

        // Ключ userExtraOptions не должен быть добавлен в JSON
        assertFalse(json.has(ModelFields.GameConfigOptions.userExtraOptions));
    }

    @Test
    public void parse_handlesEmptyPlaceholdersList() throws Exception {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.placeholders = new ArrayList<>(); // Пустой список
        model.safeAreaInsets = new SafeAreaInsets();
        model.userExtraOptions = new HashMap<>();

        FromGameConfigOptions parser = new FromGameConfigOptions(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        JSONArray placeholdersJson = json.getJSONArray(ModelFields.GameConfigOptions.placeholders);
        assertEquals(0, placeholdersJson.length());
    }

    @Test
    public void parse_handlesEmptyUserExtraOptions() throws Exception {
        GameConfigOptions model = new GameConfigOptions();
        model.sessionId = "sess_1";
        model.safeAreaInsets = new SafeAreaInsets();
        model.userExtraOptions = new HashMap<>(); // Пустая мапа

        FromGameConfigOptions parser = new FromGameConfigOptions(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        JSONObject extraOptionsJson = json.getJSONObject(ModelFields.GameConfigOptions.userExtraOptions);
        assertEquals(0, extraOptionsJson.length());
    }
}