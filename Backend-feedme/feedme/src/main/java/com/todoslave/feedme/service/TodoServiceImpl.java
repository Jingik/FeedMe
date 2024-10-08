package com.todoslave.feedme.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoslave.feedme.DTO.TodoCalendarResponseDTO;
import com.todoslave.feedme.DTO.TodoCreateRequestDTO;
import com.todoslave.feedme.DTO.TodoDailyRequestDTO;
import com.todoslave.feedme.DTO.TodoResponseDTO;
import com.todoslave.feedme.DTO.TodoMainResponseDTO;
import com.todoslave.feedme.DTO.TodoModifyRequestDTO;
import com.todoslave.feedme.DTO.TodoRequestDTO;
import com.todoslave.feedme.domain.entity.diary.PictureDiary;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.domain.entity.task.CreatureTodo;
import com.todoslave.feedme.domain.entity.task.DayOff;
import com.todoslave.feedme.domain.entity.task.Todo;
import com.todoslave.feedme.gpt.dto.ChatGPTRequest;
import com.todoslave.feedme.gpt.dto.ChatGPTResponse;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.CreatureTodoReposito;
import com.todoslave.feedme.repository.DiaryRepository;
import com.todoslave.feedme.repository.TodoCategoryRepository;
import com.todoslave.feedme.repository.TodoRepository;
import com.todoslave.feedme.util.FlaskClientUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TodoServiceImpl implements TodoService {

  @Autowired
  private final TodoRepository todoRepository;
  @Autowired
  private final TodoCategoryRepository todoCategoryRepository;
  @Autowired
  private final CreatureTodoReposito creatureTodoReposito;
  @Autowired
  private final DayOffService dayOffService;
  @Autowired
  private final CreatureService creatureService;
  @Autowired
  private RestTemplate template;
  @Autowired
  private DiaryRepository diaryRepository;
  @Autowired
  private FlaskClientUtil flaskClientUtil;
  @Autowired
  private final RestTemplate restTemplate = new RestTemplate();

  // 할일 목록에서 일정(일) 불러오기
  @Override
  public List<TodoResponseDTO> getTodoListDaily(TodoDailyRequestDTO todoDailyRequestDTO) {

    LocalDate date = todoDailyRequestDTO.getDate();

    if(todoDailyRequestDTO.getNext()<0){
      date.minusDays(1);
    }else{
      date.plusDays(1);
    }

    int memberId = SecurityUtil.getCurrentUserId();

    List<Todo> query = todoRepository.findAllByMemberIdAndCreatedAt(memberId, date);
    List<TodoResponseDTO> todos = new ArrayList<>();

    for(Todo todo : query){

      TodoResponseDTO responseDto = new TodoResponseDTO();
      responseDto.setId(todo.getId());
      responseDto.setCategoryId(todo.getTodoCategory().getId());
      responseDto.setCategoryName(todo.getTodoCategory().getName());
      responseDto.setContent(todo.getContent());
      responseDto.setCreatedAt(todo.getCreatedAt());
      responseDto.setIsCompleted(todo.getIsCompleted());

      todos.add(responseDto);

    }

    return todos;
  }

  // 메인 달력에서 일정 불러오기
  @Override
  public List<TodoResponseDTO> getTodoCalendarDaily(TodoRequestDTO todoRequestDTO) {

    LocalDate date = todoRequestDTO.getDate();

    int memberId = SecurityUtil.getCurrentUserId();
    List<Todo> query = todoRepository.findAllByMemberIdAndCreatedAt(memberId, date);
    List<TodoResponseDTO> todos = new ArrayList<>();

    for(Todo todo : query){

      TodoResponseDTO responseDto = new TodoResponseDTO();
      responseDto.setId(todo.getId());
      responseDto.setCategoryId(todo.getTodoCategory().getId());
      responseDto.setCategoryName(todo.getTodoCategory().getName());
      responseDto.setContent(todo.getContent());
      responseDto.setCreatedAt(todo.getCreatedAt());
      responseDto.setIsCompleted(todo.getIsCompleted());

      todos.add(responseDto);

    }

    return todos;

  }

  // 메인화면에서 당일 안한 일정들 불러오기
  @Override
  public List<TodoMainResponseDTO> getTodoMainDaily() {

    LocalDate date = LocalDate.now();

    System.out.println("date : "+date);

    int memberId = SecurityUtil.getCurrentUserId();
    List<Todo> query = todoRepository.findAllByMemberIdAndCreatedAtIsCompleted(memberId, date, 0);

    List<TodoMainResponseDTO> todos = new ArrayList<>();

    for(Todo todo : query){

      TodoMainResponseDTO responseDto = new TodoMainResponseDTO();
      responseDto.setId(todo.getId());
      responseDto.setContent(todo.getContent());
      responseDto.setIsCompleted(todo.getIsCompleted());

      todos.add(responseDto);

    }

    return todos;

  }

  // 월별 일정 완/미완 불러오기
  @Override
  public List<TodoCalendarResponseDTO> getTodoCalendarCompleted(TodoRequestDTO todoRequestDTO) {

    int year = todoRequestDTO.getDate().getYear();
    int month = todoRequestDTO.getDate().getMonthValue();

    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDate firstDay = yearMonth.atDay(1);
    LocalDate lastDay = yearMonth.atEndOfMonth();
    //리스트 만들고
    List<TodoCalendarResponseDTO> todoCounts = new ArrayList<>();

    //1~31일까지 쭉 불러와
    for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {

      TodoCalendarResponseDTO todoCalendarResponseDTO = new TodoCalendarResponseDTO();

      //안한거 갯수 더라기
      long inCompleted = todoRepository.countByMemberIdAndCreatedAtAndIsCompleted(
          SecurityUtil.getCurrentUserId(),date, 0)
          +creatureTodoReposito.countByMemberIdAndCreatedAtAndIsCompleted(
              SecurityUtil.getCurrentUserId(),date,0);

      todoCalendarResponseDTO.setInCompleted((int)inCompleted);

      long completed =  todoRepository.countByMemberIdAndCreatedAtAndIsCompleted(SecurityUtil.getCurrentUserId(),date, 1)+creatureTodoReposito.countByMemberIdAndCreatedAtAndIsCompleted(SecurityUtil.getCurrentUserId(),date,1);

      todoCalendarResponseDTO.setCompleted((int)completed);

      todoCalendarResponseDTO.setTotal((int)(inCompleted+completed));
      todoCalendarResponseDTO.setDate(date);

      todoCounts.add(todoCalendarResponseDTO);

    }


    return todoCounts;
  }

  // 일정 추가하기
  @Override
  public TodoResponseDTO insertTodo(TodoCreateRequestDTO todoCreateRequestDTO) {

    Todo todo = new Todo();
    todo.setMember(SecurityUtil.getCurrentMember());
    todo.setTodoCategory(todoCategoryRepository.findById(todoCreateRequestDTO.getCategoryId()).orElseThrow());
    todo.setContent(todoCreateRequestDTO.getContent());
    todo.setCreatedAt(todoCreateRequestDTO.getTodoDay());
    todo = todoRepository.save(todo);

    TodoResponseDTO todoResponseDTO = new TodoResponseDTO();
    todoResponseDTO.setId(todo.getId());
    todoResponseDTO.setContent(todo.getContent());
    todoResponseDTO.setCategoryId(todo.getTodoCategory().getId());
    todoResponseDTO.setCategoryName(todo.getTodoCategory().getName());
    todoResponseDTO.setCreatedAt(todo.getCreatedAt());
    todoResponseDTO.setIsCompleted(todo.getIsCompleted());

    return todoResponseDTO;
  }

  //일정 삭제하기
  @Override
  public void deleteTodo(int todoId) {
    todoRepository.deleteById(todoId);
  }

  //일정 수정하기
  @Override
  @Transactional
  public TodoResponseDTO updateTodo(TodoModifyRequestDTO todoModifyRequestDTO) {
    Todo todo = todoRepository.findById(todoModifyRequestDTO.getId()).orElseThrow();
    todo.setContent(todoModifyRequestDTO.getContent());

    TodoResponseDTO todoResponseDTO = new TodoResponseDTO();
    todoResponseDTO.setId(todo.getId());
    todoResponseDTO.setContent(todo.getContent());
    todoResponseDTO.setCategoryId(todo.getTodoCategory().getId());
    todoResponseDTO.setCategoryName(todo.getTodoCategory().getName());
    todoResponseDTO.setCreatedAt(todo.getCreatedAt());
    todoResponseDTO.setIsCompleted(todo.getIsCompleted());

    return todoResponseDTO;
  }

  //일정 완료하기
  @Override
  @Transactional
  public TodoResponseDTO completeTodo(int todoId) {

    Todo todo = todoRepository.findById(todoId).orElseThrow();
    if(todo.getIsCompleted()==1){
      todo.setIsCompleted(0);
    }else{
      todo.setIsCompleted(1);
    }

    TodoResponseDTO todoResponseDTO = new TodoResponseDTO();
    todoResponseDTO.setId(todo.getId());
    todoResponseDTO.setContent(todo.getContent());
    todoResponseDTO.setCategoryId(todo.getTodoCategory().getId());
    todoResponseDTO.setCategoryName(todo.getTodoCategory().getName());
    todoResponseDTO.setCreatedAt(todo.getCreatedAt());
    todoResponseDTO.setIsCompleted(todo.getIsCompleted());

    return todoResponseDTO;
  }

  @Override
  public boolean AllcompleteTodo(TodoRequestDTO todoRequestDTO) {

    //끝내는 날자 입력
    LocalDate date = todoRequestDTO.getDate();

    //만약에 완료를 이미 했다면
    if(!dayOffService.isActionAllowed(SecurityUtil.getCurrentUserId(),date)){
      return false;
    }

    //일정 끝내기
    List<Todo> todoList = todoRepository.findByMemberIdAndCreatedAt(SecurityUtil.getCurrentUserId(),date);
    //크리쳐 일정 끝내기
    List<CreatureTodo> creatureTodoList = creatureTodoReposito.findByMemberIdAndCreatedAt(SecurityUtil.getCurrentUserId(),date);


    // StringBuilder로 문자열 누적
    StringBuilder todoAllBuilder = new StringBuilder();
    int completedTodos = 0;
    int completedCreatureTodos = 0;

    List<String> todolist = new ArrayList<>();

    for (Todo todo : todoList) {
      if (todo.getIsCompleted() == 1) {
        todoAllBuilder.append(todo.getTodoCategory().getName()).append(" - ").append(todo.getContent()).append("\n");
        completedTodos++;
        todolist.add(todo.getContent());
      }
    }

    for (CreatureTodo creatureTodo : creatureTodoList) {
      if (creatureTodo.getIsCompleted() == 1) {
        todoAllBuilder.append(creatureTodo.getContent()).append("\n");
        completedCreatureTodos++;
        todolist.add(creatureTodo.getContent());
      }
    }

    // todoAll을 String으로 변환
    String todoAll = todoAllBuilder.toString();

    String generatedDiaryEntry = generateDiaryEntry(todoAll);// 여기서 AI가 했던일을 일기로 만들어줌

    PictureDiary Diary = new PictureDiary();
    Diary.setContent(generatedDiaryEntry);
    Diary.setCreatedAt(date);
    Diary.setMember(SecurityUtil.getCurrentMember());
    diaryRepository.save(Diary); //다이어리 만들어줘

    //완료처리
    DayOff dayOff = new DayOff();
    dayOff.setEndDay(date);
    dayOff.setMember(SecurityUtil.getCurrentMember());
    dayOffService.saveDayOff(dayOff);

    // 그림일기 요청 추가 (Flask 서버로 전송)
//    createPictureDiary(date, todolist);  // 나중에 구현

    //그림일기 창, feed에서 불러옵니다.

    //경험치 올리기
    creatureService.expUp(completedTodos+completedCreatureTodos);

    //예본 해
      return true;
    }


//  private void createPictureDiary(LocalDate date) {
//
//    Member member = SecurityUtil.getCurrentMember();
//    String keyword = member.getCreature().getCreatureKeyword();
//    String nickname = member.getNickname();
//    // 날짜를 문자열 형식으로 변환 (yyyy-MM-dd)
//    String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//    // URL 생성 - 키워드와 날짜를 쿼리 파라미터로 추가
//    String url = String.format("http://localhost:3333/yolo?name=%s&keyword=%s&date=%s",
//            nickname, keyword, formattedDate);
//
//    // Flask 서버에 GET 요청 보내기
//    ResponseEntity<ByteArrayResource> response = template.getForEntity(url, ByteArrayResource.class);
//
//    // 요청이 성공했는지 확인하고, 성공하지 않았으면 예외를 던짐
//    if (!response.getStatusCode().is2xxSuccessful()) {
//      throw new RuntimeException("Failed to create picture diary on Flask server.");
//    }
//
//    // Flask 서버로부터 받은 그림일기 데이터를 활용할 수 있습니다.
//  }

  @SneakyThrows
  private void createPictureDiary(LocalDate date, List<String> todolist) {
    // Flask 서버 URL 설정
    String flaskUrl = "http://flask:33333/store/creature_diary";

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    Member member = SecurityUtil.getCurrentMember();
    // String keyword = member.getCreature().getCreatureKeyword();
    String nickname = member.getNickname();

    // 날짜를 문자열 형식으로 변환 (yyyy-MM-dd)
    String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    // ObjectMapper를 사용해 todolist를 JSON 문자열로 변환
    ObjectMapper objectMapper = new ObjectMapper();
    String todolistJson = objectMapper.writeValueAsString(todolist);

    // 요청 본문 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("nickname", nickname);
    body.add("creature_name",SecurityUtil.getCurrentMember().getCreature().getCreatureName());
    body.add("date", formattedDate);
    body.add("todolist", todolistJson);

    // 요청 엔티티 생성
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // Flask 서버에 POST 요청 보내기 (응답이 필요할 때)
    // ResponseEntity<ByteArrayResource> response = restTemplate.postForEntity(flaskUrl, requestEntity, ByteArrayResource.class);
    // 요청이 성공했는지 확인하고, 성공하지 않았으면 예외를 던짐
    // if (!response.getStatusCode().is2xxSuccessful()) {
    // throw new RuntimeException("Failed to create picture diary on Flask server.");
    // }

    //응답이 필요하지 않을 때
    restTemplate.postForEntity(flaskUrl, requestEntity, Void.class);


  }


  // GPT API 호출을 통해 일기를 생성하는 메서드
  @Value("${openai.api.url}")
  private String apiURL;
  private String generateDiaryEntry(String todoAll) {
    String prompt = "다음 항목들을 바탕으로 한글로 일기를 작성해주세요. 150자 이내로 없는 얘기를 꾸며쓰지는말고, 자연스럽게 요약해 주세요:\n" + todoAll;

    // GPT API 호출
    ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo", prompt);
    ChatGPTResponse response = template.postForObject(apiURL, request, ChatGPTResponse.class);

    // 응답 처리
    if (response != null && !response.getChoices().isEmpty()) {
      String diaryEntry = response.getChoices().get(0).getMessage().getContent();

      // 줄바꿈 제거
      diaryEntry = diaryEntry.replaceAll("\n+", " ").replaceAll("\\s+", " ").trim();

      // 150자 이내로 요약
      if (diaryEntry.length() > 150) {
        diaryEntry = truncateToNearestSentence(diaryEntry, 150);
      }

      return diaryEntry;
    }

    // 기본 응답 또는 오류 처리
    return "일기 작성에 실패했습니다.";
  }



  //만약에 150자가 넘어간다면
  private String truncateToNearestSentence(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }

    // 최대 길이에서 가장 가까운 문장의 끝 (마침표)을 찾습니다.
    int end = text.lastIndexOf(". ", maxLength);
    if (end == -1) {
      // 문장이 없으면 최대 길이에서 잘라냅니다.
      return text.substring(0, maxLength).trim();
    }

    // 문장이 있으면 해당 문장까지만 반환합니다.
    return text.substring(0, end + 1).trim();
  }

}
