package dat3.remoteapiexampleopenai2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat3.remoteapiexampleopenai2.dto.QuestionRequest;
import dat3.remoteapiexampleopenai2.dto.QuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionService {

  @Value("${API_KEY}")
  private String API_KEY;

  String URL = "https://api.openai.com/v1/completions";

  String FIXED_PROMPT = "Please answer the question: ";

  WebClient client = WebClient.create();

  public QuestionResponse askQuestion(QuestionRequest request) throws URISyntaxException {

    String inputPrompt = FIXED_PROMPT + request.getQuestion();

    Map<String, Object> body = new HashMap<>();

    body.put("model", "text-davinci-003");
    body.put("prompt", inputPrompt);
    body.put("temperature", 1);
    body.put("max_tokens", 50);
    body.put("top_p", 1);
    body.put("frequency_penalty", 0.2);
    body.put("presence_penalty", 0);

    ObjectMapper mapper = new ObjectMapper();
    String json = "";
    try {
      json = mapper.writeValueAsString(body);
    } catch (Exception e) {
      e.printStackTrace();
    }


    QuestionResponse response = client.post()
        .uri(new URI(URL))
        .header("Authorization", "Bearer " + API_KEY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(json))
        .retrieve()
        .bodyToMono(QuestionResponse.class)
        .block();
    return response;
  }

}
