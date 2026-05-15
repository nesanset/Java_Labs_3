package mephi.lab3.reporting;

import java.util.StringJoiner;
import mephi.lab3.app.LoadedMission;
import mephi.lab3.domain.Mission;

public class BaseMissionReportFormat implements MissionReportFormat{
    private final String title;
    private final boolean includeFilePath;

    public BaseMissionReportFormat(String title, boolean includeFilePath){
        this.title = title;
        this.includeFilePath = includeFilePath;
    }

    @Override
    public String format(LoadedMission loadedMission){
        Mission mission = loadedMission.getMission();
        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        joiner.add(title);
        joiner.add("Файл: " + loadedMission.getWayToFile());
        joiner.add("Распознанный формат: " + loadedMission.getFormat().getViewName());
        joiner.add("");
        joiner.add("МИССИЯ:");
        joiner.add("-Статус: " + requiredValueOrDefault(mission.getOutcome()));
        joiner.add("-ID миссии: " + requiredValueOrDefault(mission.getMissionId()));
        joiner.add("-Дата: " + requiredValueOrDefault(mission.getDate()));
        joiner.add("-Локация: " + requiredValueOrDefault(mission.getLocation()));
        joiner.add("-Ущерб: " + optionalValueOrDefault(mission.getDamageCost()));
        joiner.add("-Примечание: " + valueOrDefault(mission.getNote(), "-"));
        return joiner.toString();
    }

    private String requiredValueOrDefault(String value){
        return valueOrDefault(value, "-");
    }

    private String optionalValueOrDefault(String value){
        return valueOrDefault(value, "-");
    }

    private String valueOrDefault(String value, String defaultValue){
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}