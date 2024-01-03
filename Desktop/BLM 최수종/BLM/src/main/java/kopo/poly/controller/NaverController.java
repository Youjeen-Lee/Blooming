package kopo.poly.controller;


import kopo.poly.service.INaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


@Slf4j
@Controller
@RequiredArgsConstructor
public class NaverController {

    private final INaverService naverService;

    @GetMapping("/user/naverLogin")
    public String naverLogin(@RequestParam String code, HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".naverLogin start!!");
        // 엑세스 토큰 받음
        String access_Token = naverService.getAccessToken(code);
        log.info("access_Token : " + code);
        // 토큰을 이용한 카카오 로그인한 유저 정보 가져오기
        HashMap<String, Object> userInfo = naverService.getUserInfo(access_Token);

        log.info("email : " + userInfo.get("email"));
        log.info("name : " + userInfo.get("name"));

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        /**
         프로젝트마다 필요정보 세션에 올리기
         */
        session.setAttribute("SS_USER_ID", email);
        session.setAttribute("SS_USER_NAME", name);
        session.setAttribute("ACCESS_TOKEN", access_Token);
        session.setAttribute("ACCESS_KIND", "NAVER"); // 접근 종류

        model.addAttribute("url","/main");
        model.addAttribute("msg",  name + "님 환영합니다.");

        log.info(this.getClass().getName() + ".user/naverLogin End!!");
        return "/redirect"; // html 페이지 url 설정
    }

    @GetMapping(value = "/user/naverLogout")
    public String unlink(HttpSession session, ModelMap model) {
        log.info(this.getClass().getName() + ".naverLogout Start!");

        naverService.unlink((String) session.getAttribute("ACCESS_TOKEN"));
        session.invalidate();

        String msg = "로그아웃 되셨습니다.\n" + "다음에 또 만나요!";
        String url = "/main";

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName() + ".naverLogout End!");
        return "/redirect";
    }


}