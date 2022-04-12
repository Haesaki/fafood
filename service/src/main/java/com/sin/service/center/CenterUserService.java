package com.sin.service.center;

import com.sin.pojo.Users;
import com.sin.pojo.bo.center.CenterUserBO;
import org.apache.catalina.User;

public interface CenterUserService {
    Users queryUserInfo(String userId);

    Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    Users updateUserFace(String userId, String faceUrl);
}
