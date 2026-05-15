package mephi.lab3.parsing;

import mephi.lab3.assembly.MissionBuilder;

public interface MissionParser{
    void parse(String content, MissionBuilder missionBuilder) throws MissionParseException;
}
