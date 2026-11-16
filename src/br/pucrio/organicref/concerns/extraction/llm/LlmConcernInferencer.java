package br.pucrio.organicref.concerns.extraction.llm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.pucrio.organicref.concerns.extraction.ConcernInferencer;
import br.pucrio.organicref.model.entities.Concern;

/** OpenAI-compatible chat-completions concern classifier. */
public class LlmConcernInferencer implements ConcernInferencer {
  private static final Pattern NON_WORD = Pattern.compile("[^\\p{L}\\p{N}]+");
  private final String apiKey;
  private final String model;
  private final URI endpoint;
  private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
  private final ObjectMapper mapper = new ObjectMapper();
  private final Map<String, Concern> concernsByLabel = new LinkedHashMap<>();
  private final Map<String, Map<Concern, Double>> cache = new LinkedHashMap<>();

  public LlmConcernInferencer(String apiKey, String model, String baseUrl) {
    if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("Set OPENAI_API_KEY to use LLM inference.");
    this.apiKey = apiKey;
    this.model = model == null || model.isBlank() ? "gpt-4o-mini" : model;
    String base = baseUrl == null || baseUrl.isBlank() ? "https://api.openai.com/v1" : baseUrl.replaceAll("/$", "");
    this.endpoint = URI.create(base + "/chat/completions");
  }

  @Override public synchronized List<Concern> getAllConcerns() { return new ArrayList<>(concernsByLabel.values()); }

  @Override public synchronized Map<Concern, Double> inferConcernsForElement(String text) {
    if (cache.containsKey(text)) return cache.get(text);
    try {
      ObjectNode body = mapper.createObjectNode();
      body.put("model", model);
      body.put("temperature", 0);
      ArrayNode messages = body.putArray("messages");
      messages.addObject().put("role", "system").put("content",
          "Classifique o elemento Java nas funcionalidades (concerns) que ele implementa. " +
          "Retorne somente JSON válido no formato {\"concerns\":[{\"label\":\"nome curto\",\"confidence\":0.0}]}. " +
          "Use de 1 a 3 rótulos em português, probabilidades entre 0 e 1 cuja soma seja 1. " +
          "Baseie-se apenas no texto; não avalie qualidade estrutural.");
      messages.addObject().put("role", "user").put("content", "Elemento Java (tokens):\n" + text);
      HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(90))
          .header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300)
        throw new IllegalStateException("LLM request failed (HTTP " + response.statusCode() + "): " + response.body());
      JsonNode root = mapper.readTree(response.body());
      String content = root.path("choices").path(0).path("message").path("content").asText();
      JsonNode parsed = mapper.readTree(content);
      JsonNode items = parsed.path("concerns");
      if (!items.isArray() || items.isEmpty()) throw new IllegalStateException("LLM returned no concerns for an element.");
      Map<Concern, Double> result = new LinkedHashMap<>();
      double total = 0;
      for (JsonNode item : items) {
        String label = item.path("label").asText("").trim();
        double confidence = item.path("confidence").asDouble(-1);
        if (label.isBlank() || confidence < 0 || confidence > 1) continue;
        Concern concern = concernsByLabel.computeIfAbsent(label.toLowerCase(Locale.ROOT),
            key -> new Concern(concernsByLabel.size(), words(label)));
        result.put(concern, confidence);
        total += confidence;
      }
      if (result.isEmpty() || total <= 0) throw new IllegalStateException("LLM returned invalid concern probabilities.");
      final double sum = total;
      result.replaceAll((concern, confidence) -> confidence / sum);
      cache.put(text, result);
      return result;
    } catch (IOException e) {
      throw new IllegalStateException("Could not parse or send LLM concern inference request.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("LLM concern inference was interrupted.", e);
    }
  }

  private List<String> words(String label) {
    List<String> result = new ArrayList<>();
    for (String word : NON_WORD.split(label.toLowerCase(Locale.ROOT))) if (!word.isBlank()) result.add(word);
    if (result.isEmpty()) result.add("funcionalidade");
    return result;
  }
}
