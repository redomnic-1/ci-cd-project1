package com.app;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
@RestController
public class HelloController {
    @GetMapping("/")
    public String home() { return "CI/CD Pipeline LIVE — Build #2 | Auto deploy"; }
    @GetMapping("/health")
    public String health() { return "OK"; }
    @GetMapping("/info")
    public String info() throws Exception {
        return "Pod: " + InetAddress.getLocalHost().getHostName();
    }
}
