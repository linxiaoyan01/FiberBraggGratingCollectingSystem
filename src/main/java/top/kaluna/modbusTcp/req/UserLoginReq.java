package top.kaluna.modbusTcp.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Yuery
 * @date 2022/2/25/0025 - 22:41
 */
public class UserLoginReq {

    @NotEmpty(message = "密码不能为空")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotEmpty(message = "登陆名不能为空")
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserLoginReq{");
        sb.append("password='").append(password).append('\'');
        sb.append(", loginName='").append(loginName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

