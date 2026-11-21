package br.com.fiap.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @GetMapping("/login")
    @ResponseBody
    public String carregaPaginaListagem(){
        return "API Bairro Online";
    }
}