package mephi.lab3.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import mephi.lab3.app.*;
import mephi.lab3.domain.Mission;
import mephi.lab3.entity.MissionEntity;
import mephi.lab3.mapper.MissionMapper;
import mephi.lab3.parsing.MissionParseException;
import mephi.lab3.repository.*;
import mephi.lab3.reporting.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MissionService{
    private final MissionRepository missionRepository;
    private final MissionMapper missionMapper;
    private final MissionLoader missionLoader = new MissionLoader();
    private final ReportFormatService reportFormatService = new ReportFormatService();

    public MissionService(MissionRepository missionRepository, MissionMapper missionMapper){
        this.missionRepository = missionRepository;
        this.missionMapper = missionMapper;
    }

    @Transactional(readOnly = true)
    public List<Mission> findAll(){
        List<MissionEntity> missions = missionRepository.findAll();
        List<Mission> result = new ArrayList<>();

        for (MissionEntity mission : missions){
            result.add(missionMapper.toDomain(mission));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Mission findByMissionId(String missionId){
        return missionMapper.toDomain(findEntity(missionId));
    }

    @Transactional
    public Map<String, Object> importFile(MultipartFile file){
        if (file == null || file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл миссии не выбран");
        }
        LoadedMission loadedMission = readMission(file);
        ensureMissionId(loadedMission);
        MissionEntity existing = missionRepository.findByMissionId(loadedMission.getMission().getMissionId()).orElse(null);
        MissionEntity savedMission;
        if (existing == null){
            savedMission = missionRepository.save(missionMapper.toEntity(loadedMission));
        }else{
            missionMapper.updateEntity(existing, loadedMission);
            savedMission = missionRepository.save(existing);
        }
        List<String> warnings = loadedMission.getValidationResult().getWarnings();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("missionId", savedMission.getMissionId());
        response.put("sourceFormat", savedMission.getSourceFormat());
        response.put("message", "Миссия успешно импортирована");
        response.put("validationWarnings", warnings);
        return response;
    }

    @Transactional(readOnly = true)
    public String buildTextReport(String missionId, String reportType){
        if (!reportFormatService.supports(reportType)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неподдерживаемый тип отчета: " + reportType);
        }
        MissionReportFormat reportFormat = reportFormatService.resolve(reportType);
        return reportFormat.format(missionMapper.toLoadedMission(findEntity(missionId)));
    }

    @Transactional
    public void deleteByMissionId(String missionId){
        missionRepository.delete(findEntity(missionId));
    }

    public String getDefaultReportType(){
        return reportFormatService.getDefaultReportType();
    }

    private LoadedMission readMission(MultipartFile file){
        try{
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            return missionLoader.loadContent(file.getOriginalFilename(), content);
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось прочитать файл миссии", e);
        }catch (MissionParseException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось разобрать файл миссии: " + e.getMessage(), e);
        }
    }

    private void ensureMissionId(LoadedMission loadedMission){
        String missionId = loadedMission.getMission().getMissionId();
        if (missionId == null || missionId.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "В миссии отсутствует обязательное поле missionId");
        }
    }

    private MissionEntity findEntity(String missionId){
        MissionEntity mission = missionRepository.findByMissionId(missionId).orElse(null);

        if (mission == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Миссия " + missionId + " не найдена");
        }

        return mission;
    }
}