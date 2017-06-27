import com.google.gson.JsonObject;

/**
 * Created by AdminPC on 6/26/2017.
 */
public class Main {
  public static void main(String[] args) throws Exception {

    JsonObjects jsonObjects = new JsonObjects();
    JsonObject jObject = jsonObjects.CreateJSON();

    System.out.println(jsonObjects.GetJsonString(jObject));

    Employee staff = jsonObjects.GetEmployee(jObject);
    System.out.println(staff.nic);
    System.out.println(staff.getSalary());
    System.out.println(staff.Name);
    System.out.println(staff.isPermanent);
    System.out.println(staff.Surname);
  }
}
