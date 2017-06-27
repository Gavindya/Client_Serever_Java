/**
 * Created by AdminPC on 6/26/2017.
 */
public class Employee {
  public String Name;
  public String nic;
  private Long Salary;
  public boolean isPermanent;
  public String Surname;

  Employee(String name, String NIC, Long salary, Boolean permanent, String surname){
    Name = name;
    nic = NIC;
    Salary = salary;
    isPermanent = permanent;
    Surname=surname;
  }

  public Employee GetEmployee(){
    return this;
  }

  public void setSalary(Long salary){
    this.Salary=salary;
  }
  public Long getSalary(){
    return this.Salary;
  }
  public long calcSalary(){
    return (this.Salary*12);
  }
}
