package com.appetite;

/**
 * Created by Kavi on 3/2/16.
 */
public class User {

    String username,password,phone,address,email,pic,latitude,longitude;

    public User(String username, String password, String phone, String address) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public User(String username){
        this.username = username;
    }
    public User(String username,String email,String pic,String phoneNo,String location,String latitude,String longitude){
        this.username = username;
        this.email = email;
        this.pic = pic;
        this.phone = phoneNo;
        this.address = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
