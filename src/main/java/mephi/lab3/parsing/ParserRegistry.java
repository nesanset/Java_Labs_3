package mephi.lab3.parsing;

import java.util.EnumMap;
import java.util.Map;

public class ParserRegistry{
    private final Map<FileFormat, MissionParser> parsers = new EnumMap<>(FileFormat.class);

    public void register(FileFormat fileFormat, MissionParser parser){
        parsers.put(fileFormat, parser);
    }

    public MissionParser resolve(FileFormat fileFormat){
        MissionParser parser = parsers.get(fileFormat);
        if (parser == null){
            throw new IllegalStateException("Для формата " + fileFormat + " не найден парсер");
        }
        return parser;
    }
}