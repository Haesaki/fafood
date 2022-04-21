package com.sin.pojo.vo;

import com.sin.pojo.Users;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class UsersVO extends Users{

    private String userUniqueToken;

    public UsersVO(Users user){
    }
}
