package mephi.lab3.reporting;

import java.util.Map;
import java.util.StringJoiner;
import mephi.lab3.app.LoadedMission;
import mephi.lab3.domain.*;

public class DetailedReportDecorator extends ReportFormatDecorator{
    public DetailedReportDecorator(MissionReportFormat wrappedReportFormat){
        super(wrappedReportFormat);
    }

    @Override
    public String format(LoadedMission loadedMission){
        Mission mission = loadedMission.getMission();
        StringBuilder builder = new StringBuilder(formatWrapped(loadedMission));
        appendCurse(builder, mission);
        appendParticipants(builder, mission);
        appendTechniques(builder, mission);
        appendExtensions(builder, mission);

        return builder.toString();
    }

    private void appendCurse(StringBuilder builder, Mission mission){
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("ПРОКЛЯТИЕ:").append(System.lineSeparator()).append("-Имя: ").append(readCurseValue(mission, true)).append(System.lineSeparator()).append("-Уровень угрозы: ").append(readCurseValue(mission, false));
    }

    private void appendParticipants(StringBuilder builder, Mission mission){
        if (mission.getParticipants().isEmpty()){
            return;
        }
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("УЧАСТНИКИ:");
        for (Sorcerer participant : mission.getParticipants()){
            builder.append(System.lineSeparator()).append("-").append(requiredValueOrDefault(participant.getName())).append(" (").append(requiredValueOrDefault(participant.getRank())).append(")");
        }
    }

    private void appendTechniques(StringBuilder builder, Mission mission){
        if (mission.getTechniques().isEmpty()){
            return;
        }
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("ТЕХНИКИ:");
        for (Technique technique : mission.getTechniques()){
            builder.append(System.lineSeparator()).append("-").append(requiredValueOrDefault(technique.getName())).append(System.lineSeparator()).append("  -Владелец: ").append(requiredValueOrDefault(technique.getOwner())).append(System.lineSeparator()).append("  -Тип: ").append(requiredValueOrDefault(technique.getType())).append(System.lineSeparator()).append("  -Урон: ").append(optionalValueOrDefault(technique.getDamage()));
        }
    }

    private void appendExtensions(StringBuilder builder, Mission mission){
        if (mission.getExtensionBlocks().isEmpty()){
            return;
        }
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("ДОПОЛНИТЕЛЬНЫЕ БЛОКИ:");
        for (MissionExtensionBlock extensionBlock : mission.getExtensionBlocks().values()) {
            builder.append(System.lineSeparator()).append("* ").append(extensionBlock.getName());
            for (Map.Entry<String, String> field : extensionBlock.getFields().entrySet()) {
                builder.append(System.lineSeparator()).append("  -").append(field.getKey()).append(": ").append(genericValueOrDefault(field.getValue(), "-"));
            }
            int entryNumber = 1;
            for (Map<String, String> entry : extensionBlock.getEntries()) {
                StringJoiner entryJoiner = new StringJoiner(", ");
                for (Map.Entry<String, String> field : entry.entrySet()) {
                    entryJoiner.add(field.getKey() + "=" + genericValueOrDefault(field.getValue(), "-"));
                }
                builder.append(System.lineSeparator()).append("  -entry ").append(entryNumber).append(": ").append(entryJoiner);
                entryNumber++;
            }
        }
    }

    private String readCurseValue(Mission mission, boolean readName){
        if (mission.getCurse() == null){
            return "-";
        }
        if (readName){
            return genericValueOrDefault(mission.getCurse().getName(), "-");
        }
        return genericValueOrDefault(mission.getCurse().getThreatLevel(), "-");
    }

    private String requiredValueOrDefault(String value){
        return genericValueOrDefault(value, "-");
    }

    private String optionalValueOrDefault(String value){
        return genericValueOrDefault(value, "-");
    }

    private String genericValueOrDefault(String value, String defaultValue){
        if (value == null || value.isBlank()){
            return defaultValue;
        }
        return value;
    }
}