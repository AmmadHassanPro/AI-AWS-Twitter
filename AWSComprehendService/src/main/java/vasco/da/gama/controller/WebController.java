package vasco.da.gama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

  @RequestMapping("/home")
  public String home(){
    return "/public/html/home.html";
  }
}
