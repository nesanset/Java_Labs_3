package mephi.lab3.reporting;

import mephi.lab3.app.LoadedMission;
import mephi.lab3.domain.*;

public class SummaryReportDecorator extends ReportFormatDecorator{
    public SummaryReportDecorator(MissionReportFormat wrappedReportFormat){
        super(wrappedReportFormat);
    }

    @Override
    public String format(LoadedMission loadedMission){
        Mission mission = loadedMission.getMission();
        StringBuilder builder = new StringBuilder(formatWrapped(loadedMission));
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("СВОДКА:").append(System.lineSeparator()).append("-Проклятие: ").append(describeCurse(mission.getCurse())).append(System.lineSeparator()).append("-Участников: ").append(mission.getParticipants().size()).append(System.lineSeparator()).append("-Техник: ").append(mission.getTechniques().size()).append(System.lineSeparator()).append("-Дополнительных блоков: ").append(mission.getExtensionBlocks().size());
        return builder.toString();
    }

    private String describeCurse(Curse curse){
        if (curse == null){
            return "-";
        }
        return valueOrDefault(curse.getName()) + " [" + valueOrDefault(curse.getThreatLevel())+ "]";
    }

    private String valueOrDefault(String value){
        if (value == null || value.isBlank()){
            return "-";
        }
        return value;
    }
}