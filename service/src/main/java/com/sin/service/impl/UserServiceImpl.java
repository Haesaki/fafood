package com.sin.service.impl;

import com.sin.mapper.UsersMapper;
import com.sin.pojo.Users;
import com.sin.pojo.bo.UserBO;
import com.sin.service.UserService;
import com.sin.subenum.Sex;
import com.sin.util.DateUtil;
import com.sin.util.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private final String USER_AVATAR = "https://i0.hdslb.com/bfs/face/member/noface.jpg@240w_240h_1c_1s.webp";

    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        // 构建条件
        userCriteria.andEqualTo("username", username);

        Users users = usersMapper.selectOneByExample(userExample);

        return users != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {
        Users user = new Users();
        user.setUsername(userBO.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String userId = sid.nextShort();
        user.setId(userId);
        user.setNickname(userBO.getUsername());
        user.setFace(USER_AVATAR);
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.unknown.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);
        return user;
    }
}
