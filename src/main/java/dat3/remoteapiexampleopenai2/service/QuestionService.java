package dat3.remoteapiexampleopenai2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat3.remoteapiexampleopenai2.dto.QuestionRequest;
import dat3.remoteapiexampleopenai2.dto.QuestionResponse;
import dat3.remoteapiexampleopenai2.entity.Question;
import dat3.remoteapiexampleopenai2.repository.QuestionRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

  QuestionRepo questionRepo;

  public QuestionService(QuestionRepo questionRepo) {
    this.questionRepo = questionRepo;
  }

  @Value("${API_KEY}")
  private String API_KEY;
  String URL = "https://api.openai.com/v1/completions";

  public QuestionResponse askQuestion(QuestionRequest request) throws URISyntaxException {

    String FIXED_PROMPT = "Please answer the question: ";
    /*
    String openAiResponse = checkDatabase(request.getQuestion());
    List<String> str = Arrays.stream(openAiResponse.split(",")).toList();
    boolean doesExist = false;
    if(str.size() > 1) {
      doesExist = true;

    } else {

    }

     */

    String inputPrompt = FIXED_PROMPT + request.getQuestion();
    String json = prepareJSON(inputPrompt);
    QuestionResponse response = processResponse(json);
    saveQuestionAndAnswer(request, response);

    return response;
  }


  private void saveQuestionAndAnswer(QuestionRequest request, QuestionResponse response) {
    Question question = new Question();

    String questionRequest = request.getQuestion();
    String questionResponse = response.choices.get(0).text;

    question.setQuestion(questionRequest);
    question.setAnswer(questionResponse);

    questionRepo.save(question);
  }

  private QuestionResponse processResponse(String json) throws URISyntaxException {
    WebClient client = WebClient.create();

    return client.post()
        .uri(new URI(URL))
        .header("Authorization", "Bearer " + API_KEY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(json))
        .retrieve()
        .bodyToMono(QuestionResponse.class)
        .block();
  }

  private String prepareJSON(String inputPrompt) {
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
    return json;
  }

    /*
  private String checkDatabase(String question) throws URISyntaxException {
    List<Question> allQuestions = questionRepo.findAll();
    List<String> newList = allQuestions.stream().map(Question::getQuestion).toList();
    String str = String.join(", ", newList);
    String input = "Please answer firstly with one word a true or false: is the following question phrased in a similar way " +
        "in the following list? question:" + question + " list: " + str + " and what if the first question question in the list has" +
        "index 0, question 2 has index 1, questions 3 has index 2 and so on, what's the index number of the question that is similar? That means, your answer should have the template:" +
        "'true, [index number]' if there is a similar question and simply 'false' if there's no similar question. Please leave out any white spaces in your answer.";

    String json = prepareJSON(input);
    QuestionResponse questionResponse = processResponse(json);

    return questionResponse.choices.get(0).text;
  }

   */

}
