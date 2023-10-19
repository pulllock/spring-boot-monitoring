package fun.pullock.monitoring.controller;

import fun.pullock.monitoring.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/query")
    public String query(@RequestParam Long id) {

        return userService.query(id);
    }

    @GetMapping("/random")
    public String random(@RequestParam Long id) {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(5000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return userService.query(id);
    }
}
