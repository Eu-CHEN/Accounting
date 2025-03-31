package org.example.accounting.DO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.accounting.model.User;

import java.time.LocalDateTime;

public class UserDO {
    //主键
    private String id;
    //用户名
    private String username;
    //密码
    private String password;

    private String usertype;

    private String email;
    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addTime;

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

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toModel(UserDO userDO){
        User user = new User();
        user.setUsername(userDO.getUsername());
        user.setPassword(userDO.getPassword());
        user.setUsertype(userDO.getUsertype());
        user.setEmail(userDO.getEmail());
        user.setPhone(userDO.getPhone());
        user.setAddTime(userDO.getAddTime());
        user.setId(userDO.getId());
        return user;
    }
}
