package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.AlarmResponseDTO;
import com.todoslave.feedme.DTO.AlarmSetRequestDTO;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.DTO.PaginationRequestDTO;
import com.todoslave.feedme.domain.entity.alarm.Alarm;
import com.todoslave.feedme.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    @Autowired
    private final SecurityUtil securityUtil;
    @Autowired
    private AlarmService alarmService;

    @GetMapping(value = "/subscribe/alarm", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(){
        return alarmService.createEmitter();
    }

    @GetMapping(value = "/subscribe/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSubscribe(){
        return alarmService.renewCreateEmitter();
    }

    @PostMapping("/time")
    public ResponseEntity<Void> alarmTimeSetting(@RequestBody AlarmSetRequestDTO alarmSetRequestDTO){
        alarmService.createAlarmtime(alarmSetRequestDTO);
        return ResponseEntity.noContent().build();
    }

    // 생일, 투두
    @GetMapping()
    private ResponseEntity<Slice<AlarmResponseDTO>> loadAlarms(@RequestParam("skip") int skip, @RequestParam("limit") int limit) {
        PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
        paginationRequestDTO.setSkip(skip);
        paginationRequestDTO.setLimit(limit);
        return ResponseEntity.ok(alarmService.loadAlarms(paginationRequestDTO));
    }


}
