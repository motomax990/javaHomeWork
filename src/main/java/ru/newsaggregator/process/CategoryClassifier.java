package ru.newsaggregator.process;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryClassifier {

    public static final String DEFAULT_CATEGORY = "Прочее";

    private final Map<String, List<String>> dictionary = new LinkedHashMap<>();

    public CategoryClassifier() {
        dictionary.put("Политика", List.of("президент", "выбор", "парламент", "депутат", "санкци",
                "министр", "правительств", "переговор", "саммит", "дипломат", "законопроект", "госдум"));
        dictionary.put("Экономика", List.of("экономик", "рубл", "доллар", "инфляц", "бирж",
                "нефт", "бюджет", "налог", "ввп", "инвестиц", "курс валют", "ключевой ставк"));
        dictionary.put("Спорт", List.of("матч", "турнир", "чемпион", "футбол", "хоккей", "олимпиад",
                "спортсмен", "сборн", "тренер", "теннис"));
        dictionary.put("Наука", List.of("учёны", "ученые", "исследован", "наук", "открыт", "космос",
                "физик", "биолог", "эксперимент", "генетик", "телескоп"));
        dictionary.put("Технологии", List.of("технолог", "смартфон", "приложен", "нейросет",
                "искусственн", "гаджет", "процессор", "робот", "стартап", "интернет"));
        dictionary.put("Культура", List.of("фильм", "кино", "музык", "театр", "выставк", "концерт",
                "книг", "артист", "режиссёр", "премьер", "фестивал"));
        dictionary.put("Происшествия", List.of("пожар", "авари", "дтп", "взрыв", "погиб", "пострадал",
                "задержа", "преступл", "следстви", "уголовн", "наводнен"));
        dictionary.put("Здоровье", List.of("здоров", "врач", "болезн", "вирус", "пандеми", "медицин",
                "больниц", "лечен", "вакцин"));
        dictionary.put("Общество", List.of("школ", "студент", "пенси", "город", "житель", "транспорт",
                "дорог", "жкх", "мигрант"));
    }

    public String classify(String title, String text) {
        String haystack = ((title == null ? "" : title) + " " + (text == null ? "" : text)).toLowerCase();
        String best = DEFAULT_CATEGORY;
        int bestScore = 0;
        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            int score = 0;
            for (String root : entry.getValue()) {
                if (haystack.contains(root)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = entry.getKey();
            }
        }
        return best;
    }
}
