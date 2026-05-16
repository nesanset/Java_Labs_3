package mephi.lab3.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import mephi.lab3.app.*;
import mephi.lab3.dto.*;
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
    public List<MissionSummaryResponse> findAll(){
        List<MissionEntity> missions = missionRepository.findAll();
        List<MissionSummaryResponse> result = new ArrayList<>();

        for (MissionEntity mission : missions){
            result.add(missionMapper.toSummaryResponse(mission));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public MissionResponse findByMissionId(String missionId){
        return missionMapper.toResponse(findEntity(missionId));
    }

    @Transactional
    public ImportResponse importFile(MultipartFile file){
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
        return new ImportResponse(savedMission.getMissionId(), savedMission.getSourceFormat(), "Миссия успешно импортирована", warnings);
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
