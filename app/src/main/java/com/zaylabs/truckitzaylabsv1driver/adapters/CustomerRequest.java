package com.zaylabs.truckitzaylabsv1driver.adapters;

/**
 * Created by Adil Raza on 3/15/2018.
 */

public class CustomerRequest {

    private String CustomerID;
   /* private String CustomerName;
    private String CustomerPhone;
    private String PikupLocation;
    private String DropLocation;
*/
    public CustomerRequest(){
}

public CustomerRequest(String CustomerID/*,String CustomerName, String CustomerPhone,String PickupLocation, String DropLocation*/) {

    this.CustomerID = CustomerID;
    /*this.CustomerName = CustomerName;
    this.CustomerPhone = CustomerPhone;
    this.PikupLocation = PickupLocation;
    this.DropLocation = DropLocation;*/
}

public String CustomerID(){
        return CustomerID;
}
/*
public String CustomerName(){
    return CustomerName;
}
public String CustomerPhone(){
    return CustomerPhone;
}
public String PickupLocation(){
    return PikupLocation;
}
public String DropLocation(){
    return DropLocation;
}
*/

}