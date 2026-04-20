//package edu.bi.springdemo.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloController {
//
//    @GetMapping("/hello")
//    public String sayHello(@RequestParam(defaultValue = "World") String name) {
//        return "Hello "+name+"! :_)";
//    }
//    //http://localhost:8081/hello?name=Karo
//
//    @GetMapping("/add")
//    public Integer addNumbers(@RequestParam Integer a, @RequestParam Integer b) {
//        return a+b;
//    }
//    //http://localhost:8081/add?a=2&b=3
//
//    @GetMapping("/random")
//    public Integer randomNumber(@RequestParam Integer min, @RequestParam Integer max) {
//        return (int) ((Math.random() * (max - min)) + min);
//    }
//    //http://localhost:8081/random?min=2&max=5
//
//
//}
