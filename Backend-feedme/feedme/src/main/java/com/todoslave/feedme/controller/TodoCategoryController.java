package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.TodoCategoryRequestDTO;
import com.todoslave.feedme.DTO.TodoCategoryResponseDTO;
import com.todoslave.feedme.domain.entity.task.TodoCategory;
import com.todoslave.feedme.service.TodoCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class TodoCategoryController {

  @Autowired
  private TodoCategoryService todoCategoryService;

  @GetMapping
  public ResponseEntity<List<TodoCategoryResponseDTO>> findTodoCategories(){
    return ResponseEntity.ok(todoCategoryService.getCategories());
  }

  @PostMapping("/{name}")
  public ResponseEntity<TodoCategoryResponseDTO> createTodoCategory(@PathVariable("name") String name){
    System.out.println(name+"되어요?");
    return ResponseEntity.ok(todoCategoryService.insertCategory(name));
  }

  @DeleteMapping
  public ResponseEntity<Void> removeTodoCategory(@RequestParam("categoryId") int categoryId){
    todoCategoryService.deleteCategory(categoryId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping
  public ResponseEntity<TodoCategoryResponseDTO> modifyTodoCategory(@RequestBody
      TodoCategoryRequestDTO category){
    return ResponseEntity.ok(todoCategoryService.updateCategory(category));
  }



}
