package mocklab.demo;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.Map;

@Controller
public class ToDoResource {

    private final RestTemplate restTemplate;

    @Value("${mockapi.baseurl}")
    private String baseUrl;

    public ToDoResource() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("mocklab-demo-app-1")
                .build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @GetMapping("/")
    public String indexPage(Map<String, Object> model) {
        ToDoList toDoList = restTemplate.getForObject(URI.create(baseUrl + "/todo-items"), ToDoList.class);
        model.put("items", toDoList.getItems());
        model.put("has_message", model.containsKey("message"));
        return "index";
    }

    @PostMapping("/todo-items")
    public String addItem(NewToDoItemForm form, RedirectAttributes redirectAttrs) {
        ToDoItem newItem = new ToDoItem(null, form.getDescription());
        ResponseEntity<ResponseMessage> response = restTemplate.postForEntity(baseUrl + "/todo-items", newItem, ResponseMessage.class);
        redirectAttrs.addFlashAttribute("message", response.getBody().getMessage());
        return "redirect:/";
    }

    @PostMapping("/todo-items/{id}/delete")
    public String deleteItem(@PathVariable String id) {
        restTemplate.delete(baseUrl + "/todo-items/{id}", id);
        return "redirect:/";
    }


}
