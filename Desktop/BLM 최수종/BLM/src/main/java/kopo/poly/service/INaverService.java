package kopo.poly.service;

import java.util.HashMap;

public interface INaverService {
    String getAccessToken(String authorize_code);
    HashMap<String, Object> getUserInfo(String access_Token);

    void unlink(String access_Token);

}
