package tn.esprit.back.Entities.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.back.Entities.gemini.GeminiService;

@RestController
@RequestMapping("/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return geminiService.askQuestion(question);
    }
}
