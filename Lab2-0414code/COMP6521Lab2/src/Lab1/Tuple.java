package Lab1;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tuple {
    public int EmpID;
    public Date LastUpdate;
    public String EmName;
    public int Gender;
    public int Dept;
    public int SIN;
    public String Address;
    
    public Tuple(int EmpID, Date LastUpdate, String EmName, int Gender,int Dept,int SIN, String Address) {
        super();
        this.EmpID = EmpID;
        this.LastUpdate = LastUpdate;
        this.EmName = EmName;
        this.Gender = Gender;
        this.Dept = Dept;
        this.SIN = SIN;
        this.Address = Address;
    }

}
