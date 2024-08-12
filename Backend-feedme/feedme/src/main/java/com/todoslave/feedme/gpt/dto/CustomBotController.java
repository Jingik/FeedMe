//package com.todoslave.feedme.gpt.dto;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/bot")
//public class CustomBotController {
//
//    @Autowired
//    private CustomBotService botService;
//
//    @GetMapping("/chat")
//    public String chat(@RequestParam(name = "prompt") String prompt,
//                       @RequestParam(name = "format", required = false, defaultValue = "{response}") String format) {
//        return botService.chat(prompt, format);
//    }
//
//}