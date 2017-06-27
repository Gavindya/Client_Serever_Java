import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonObjects {


  public JsonObject CreateJSON() {
    JsonObject jObject = new JsonObject();

    jObject.addProperty("nic", "947854125V");
    jObject.addProperty("Name", "Ramesh");
    jObject.addProperty("isPermanent", true);
    jObject.addProperty("Salary", 15000.00);

    return jObject;
  }

  public String GetJsonString(JsonObject JObject) {
    //convert from JSONObject to JSON string
    String jsonText = JObject.toString();
    return jsonText;
  }

  public Employee GetEmployee(JsonObject jObject){

    String jsonText = GetJsonString(jObject);
    Gson gson = new Gson();
    Employee staff = gson.fromJson(jsonText,Employee.class);
    return staff;
  }

  public Employee GetEmployee(String jsonText){

    Gson gson = new Gson();
    Employee staff = gson.fromJson(jsonText,Employee.class);
    return staff;
  }

}
