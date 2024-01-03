package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO {
    private String user_id;
    private String user_pwd;
    private String user_name;
    private String user_phone;
    private String user_email;
    private int auth_number;
    private String exists_yn;
    private String user_nick;
    private String user_addr1;

    private String user_addr2;
    private String join_dt;



}
