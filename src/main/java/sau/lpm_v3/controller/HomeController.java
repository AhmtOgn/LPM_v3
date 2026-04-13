package sau.lpm_v3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome(){
        return "index";
    }

    // BU METOD EKSİK OLDUĞU İÇİN DÖNGÜYE GİRİYOR OLABİLİR:
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html dosyasını arar
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403"; // templates/403.html dosyasını arar
    }
}