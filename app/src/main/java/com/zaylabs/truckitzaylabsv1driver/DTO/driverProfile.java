package com.zaylabs.truckitzaylabsv1driver.DTO;

import android.media.Image;
import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Adil Raza on 3/24/2018.
 */

public class driverProfile {

    private String userID;
    private String car_type;
    private String cnic;
    private String email;
    private String phone;
    private Date datejoined;
    private ImageView userDP;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDatejoined() {
        return datejoined;
    }

    public void setDatejoined(Date datejoined) {
        this.datejoined = datejoined;
    }

    public ImageView getUserDP() {
        return userDP;
    }

    public void setUserDP(ImageView userDP) {
        this.userDP = userDP;
    }

}
