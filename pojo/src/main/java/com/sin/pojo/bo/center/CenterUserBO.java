package com.sin.pojo.bo.center;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;


@Data
public class CenterUserBO {
    @ApiModelProperty(value = "用户名", name = "username", example = "json", required = false)
    private String username;

    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = false)
    private String password;

    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = false)
    private String confirmPassword;

    @NotBlank(message = "nick name not exist")
    @Length(max = 12, message = "The maximum of nickname is 12")
    @ApiModelProperty(value = "用户昵称", name = "nickname", example = "杰森", required = false)
    private String nickname;

    @Length(max = 12, message = "The real name is limited")
    @ApiModelProperty(value = "真实姓名", name = "realname", example = "杰森", required = false)
    private String realname;

    @Pattern(regexp = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", name = "mobile", example = "13999999999", required = false)
    private String mobile;

    @Email
    @ApiModelProperty(value = "邮箱地址", name = "email", example = "imooc@imooc.com", required = false)
    private String email;

    @Min(value = 0, message = "Min value is 0")
    @Max(value = 2, message = "Max value is 2")
    @ApiModelProperty(value = "性别", name = "sex", example = "0:女 1:男 2:保密", required = false)
    private Integer sex;

    @ApiModelProperty(value = "生日", name = "birthday", example = "1900-01-01", required = false)
    private Date birthday;
}
