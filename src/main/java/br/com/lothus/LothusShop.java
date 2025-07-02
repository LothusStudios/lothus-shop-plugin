package br.com.lothus;

import br.com.lothus.models.*;
import br.com.lothus.models.Response;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LothusShop {

  private final String API_URL = "https://api.lothusshop.com.br";
  private final OkHttpClient client = new OkHttpClient();
  private final String appToken;
  private final Gson gson = new Gson();

  public LothusShop(String token) {
    this.appToken = token;
  }

  Response makeRequest(String route, String method, String jsonBody) {
    RequestBody requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jsonBody == null ? "" : jsonBody
    );

    Request request = new Request.Builder()
            .url(API_URL + route)
            .method(method, method.equals("GET") ? null : requestBody)
            .addHeader("App-Token", appToken)
            .addHeader("Content-Type", "application/json")
            .build();

    try (okhttp3.Response response = client.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";
      boolean success = response.isSuccessful();
      return new Response(success, response.code(), responseBody);
    } catch (IOException e) {
      e.printStackTrace();
      return new Response(false, 500, "");
    }
  }

  public boolean checkToken() {
    Response response = makeRequest("/integration/check", "GET", null);
    if (!response.success()) {
      return false;
    }

    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
    boolean valid = json.has("response") && json.get("response").getAsBoolean();
    return valid;
  }

  public QueuedCommand[] getPendingCommands() {
    Response response = makeRequest("/integration/pendingOrders", "GET", null);
    if (!response.success()) {
      return new QueuedCommand[0];
    }

    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
    if (!json.has("response")) {
      return new QueuedCommand[0];
    }

    Type listType = new TypeToken<List<PendingOrder>>() {}.getType();
    List<PendingOrder> pendingOrders = gson.fromJson(json.get("response"), listType);

    List<QueuedCommand> result = new ArrayList<>();

    for (PendingOrder order : pendingOrders) {
      if (order.buyer == null || order.commands == null) continue;

      for (Product product : order.commands) {
        if (product.commands == null) continue;

        for (String cmd : product.commands) {
          QueuedCommand qc = new QueuedCommand(
                  order.id,
                  null,
                  order.buyer,
                  cmd,
                  false,
                  new Order(order.buyer, order.email, order.buyer, null, order.id, order.commands)
          );
          result.add(qc);
        }
      }
    }

    return result.toArray(new QueuedCommand[0]);
  }

  public void updateCommandStatus(String commandId, String status) {

    JsonObject payload = new JsonObject();
    payload.addProperty("status", status);

    Response response = makeRequest("/integration/order/" + commandId, "POST", payload.toString());

    if (!response.success()) {
      System.out.println(response.body());
    } else {
    }
  }
}
