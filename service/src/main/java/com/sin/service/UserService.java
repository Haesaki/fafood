package com.sin.service;

import com.sin.pojo.Users;
import com.sin.pojo.bo.UserBO;
import org.springframework.stereotype.Service;

public interface UserService {

    public boolean queryUsernameIsExist(String username);

    public Users createUser(UserBO userBO) throws Exception;
}
