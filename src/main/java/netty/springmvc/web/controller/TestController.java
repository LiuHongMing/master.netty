package netty.springmvc.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @RequestMapping("/test")
    public Object test() {
        Map map = new HashMap();
        map.put("hello", "world");
        return map;
    }

    @RequestMapping("/test1")
    public Object test1() {
        Map map = new HashMap();
        map.put("hello1", "world1");
        return map;
    }
}
