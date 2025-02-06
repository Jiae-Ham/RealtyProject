package org.boot.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testController {
    @GetMapping("/hello")
    public String hello(Model model) {

        model.addAttribute("greeting", "Hello world");
        return "hello";   // 뷰 이름을 반환
    }
}
