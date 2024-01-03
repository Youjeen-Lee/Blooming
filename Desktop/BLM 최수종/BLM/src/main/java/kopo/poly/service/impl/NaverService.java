package kopo.poly.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.INaverService;
import kopo.poly.util.CmmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Slf4j
@Service("NaverService")
public class NaverService implements INaverService {
    @Value("${naver.login.uri}")
    private String naverLoginURI;
    @Value("${naver.api.uri}")
    private String naverAPIURI;
    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;
    @Value("${naver.redirect.uri}")
    private String naverRedirectURI;

    public String getAccessToken(String authorize_code) {

        log.info(this.getClass().getName() + ".getAccessToken Start!");

        String access_Token = "";
        String refresh_Token = "";

        try {
            URL url = new URL(naverLoginURI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + naverClientId); //  rest api key 작성
            sb.append("&client_secret=" + naverClientSecret);
            sb.append("&redirect_uri=" + naverRedirectURI); // 설정 url 작성
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".getAccessToken End!");

        return access_Token;
    }


    // 액세스 토큰으로 유저 정보 불러오기
    public HashMap<String, Object> getUserInfo(String access_Token) {
        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        log.info(this.getClass().getName() + ".getUserInfo Start!");
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = naverAPIURI;
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject response = element.getAsJsonObject().get("response").getAsJsonObject();

            String email = CmmUtil.nvl(response.getAsJsonObject().get("email").getAsString());
            String name = CmmUtil.nvl(response.getAsJsonObject().get("name").getAsString());

            userInfo.put("name", name);
            userInfo.put("email", email);

            UserInfoDTO uDTO = new UserInfoDTO(); // 각자 USER 정보에 따라서 하면 됨
            uDTO.setUser_email(email);
            uDTO.setUser_name(name);


        } catch (IOException e) {

            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".getUserInfo End!");
        return userInfo;
    }

    @Override
    public void unlink(String access_Token) {
        log.info(this.getClass().getName() + ".unlink Start!");

        String reqURL = naverLoginURI;
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            log.info("logout responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".unlink End!");
    }
}