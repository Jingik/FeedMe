package com.todoslave.feedme.service;


import com.todoslave.feedme.DTO.DiaryResponseDTO;
import com.todoslave.feedme.domain.entity.diary.PictureDiary;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryServiceImpl(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Override
    public Page<DiaryResponseDTO> getDiaryList(Pageable pageable) {
        return diaryRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    private DiaryResponseDTO convertToDTO(PictureDiary diary) {
        DiaryResponseDTO dto = new DiaryResponseDTO();
        dto.setContent(diary.getContent());
        dto.setCreatedAt(diary.getCreatedAt());
        dto.setDiaryImg("https://i11b104.p.ssafy.io/image/pictureDiary/"+ SecurityUtil.getCurrentUserId() +"_"+diary.getCreatedAt());
        return dto;
    }
}
