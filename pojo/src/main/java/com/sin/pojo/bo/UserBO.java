package com.sin.pojo.bo;

// 前端传到后端的属性集合
public class UserBO {
    private String username;
    private String password;
    private String confirmPassword;

    public boolean isValidUserBO(){
        return username != null && password != null && confirmPassword != null
                && username.length() <= 50 && password.length() <= 50 && password.length() > 6
                && password.length() == confirmPassword.length()
                && password.equals(confirmPassword);
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
