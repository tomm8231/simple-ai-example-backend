package dat3.remoteapiexampleopenai2.api;

import dat3.remoteapiexampleopenai2.dto.QuestionRequest;
import dat3.remoteapiexampleopenai2.dto.QuestionResponse;
import dat3.remoteapiexampleopenai2.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@CrossOrigin
@RequestMapping("api/question")
public class QuestionController {

  QuestionService questionService;

  public QuestionController(QuestionService questionService) {
    this.questionService = questionService;
  }

  @PostMapping()
  QuestionResponse askQuestion(@RequestBody QuestionRequest questionRequest) throws URISyntaxException {
    return questionService.askQuestion(questionRequest);
  }
}
