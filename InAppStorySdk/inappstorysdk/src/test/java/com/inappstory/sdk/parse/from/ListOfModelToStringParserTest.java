package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListOfModelToStringParserTest {

    private static class DummyModel {
        final String name;

        DummyModel(String name) {
            this.name = name;
        }
    }

    /**
     * Тестовый парсер элемента. Выбрасывает исключение, если имя равно "error".
     */
    private static class DummyItemParser extends ModelToJSONObjectParser<DummyModel> {
        DummyItemParser(DummyModel model) {
            super(model);
        }

        @Override
        protected JSONObject parseToJSONObject() throws Exception {
            if ("error".equals(this.model.name)) {
                throw new Exception("Simulated parsing error for 'error'");
            }
            JSONObject json = new JSONObject();
            json.put("name", this.model.name);
            return json;
        }
    }

    /**
     * Тестовый наследник ListOfModelToStringParser.
     */
    private static class TestListParser extends ListOfModelToStringParser<DummyModel, DummyItemParser> {
        TestListParser(List<DummyModel> listOfModel) {
            super(listOfModel);
        }

        @Override
        protected DummyItemParser createParserInstance(DummyModel object) {
            return new DummyItemParser(object);
        }
    }

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsValidJsonArrayString_whenAllModelsParseSuccessfully() {
        List<DummyModel> models = Arrays.asList(new DummyModel("first"), new DummyModel("second"));
        TestListParser parser = new TestListParser(models);

        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[{\"name\":\"first\"},{\"name\":\"second\"}]", result);
    }

    @Test
    public void parse_returnsEmptyJsonArrayString_whenInputListIsEmpty() {
        List<DummyModel> models = Collections.emptyList();
        TestListParser parser = new TestListParser(models);

        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[]", result);
    }

    // --- Обработка null и невалидных данных ---

    @Test(expected = NullPointerException.class)
    public void parse_throwsNPE_whenConstructedWithNullList() {
        // new ArrayList<>(null) в конструкторе базового класса выбросит NPE,
        // что является стандартным поведением Java Collections.
        new TestListParser(null);
    }

    // --- Проверка поведения при сбоях ---

    @Test
    public void parse_returnsNull_whenAnyItemParserThrowsException() {
        List<DummyModel> models = Arrays.asList(new DummyModel("valid"), new DummyModel("error"));
        TestListParser parser = new TestListParser(models);

        String result = parser.parse();

        // Метод parse() перехватывает Exception в цикле и возвращает null для всей строки
        assertNull(result);
    }

    @Test
    public void parse_returnsNull_whenCreateParserInstanceThrowsException() {
        List<DummyModel> models = Arrays.asList(new DummyModel("valid"));
        
        ListOfModelToStringParser<DummyModel, DummyItemParser> crashingParser =
                new ListOfModelToStringParser<DummyModel, DummyItemParser>(models) {
                    @Override
                    protected DummyItemParser createParserInstance(DummyModel object) {
                        throw new RuntimeException("Unexpected error in factory");
                    }
                };

        String result = crashingParser.parse();
        
        // Исключение из фабричного метода также должно быть перехвачено
        assertNull(result);
    }

    // --- Проверка внутренних механизмов ---

    @Test
    public void parse_createsDefensiveCopyOfList() {
        List<DummyModel> originalList = new ArrayList<>();
        originalList.add(new DummyModel("first"));

        TestListParser parser = new TestListParser(originalList);

        // Модифицируем оригинальный список после создания парсера
        originalList.add(new DummyModel("second"));

        // Парсер должен использовать защитную копию, созданную в конструкторе базового класса
        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[{\"name\":\"first\"}]", result);
    }
}