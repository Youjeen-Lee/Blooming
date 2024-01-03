package kopo.poly.controller;


import kopo.poly.service.IKakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;


@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoController {

    private final IKakaoService kakaoService;

    @GetMapping("/user/kakaoLogin")
    public String kakaoLogin(@RequestParam String code, HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName()+"kakao login start!!");
        // 엑세스 토큰 받음
        String access_Token = kakaoService.getAccessToken(code);
        // 토큰을 이용한 카카오 로그인한 유저 정보 가져오기
        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);

        log.info("access_Token : " + access_Token);
        log.info("email : " + userInfo.get("email"));
        log.info("nickname : " + userInfo.get("nickname"));

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        /**
         프로젝트마다 필요정보 세션에 올리기
         */
        session.setAttribute("SS_USER_ID", email);
        session.setAttribute("SS_USER_NAME", nickname);
        session.setAttribute("ACCESS_TOKEN", access_Token);
        session.setAttribute("ACCESS_KIND", "KAKAO"); // 접근 종류

        log.info(this.getClass().getName()+".user/KakaologinProc End!!");

        model.addAttribute("url","/main");
        model.addAttribute("msg", nickname + "님 환영합니다.");

        return "/redirect"; // html 페이지 url 설정
    }

    @GetMapping(value="/user/kakaoLogout")
    public String unlink(HttpSession session, ModelMap model) {
        log.info(this.getClass().getName()+".kakaoLogout Start!");

        kakaoService.unlink((String)session.getAttribute("ACCESS_TOKEN"));
        session.invalidate();

        String msg = "로그아웃 되셨습니다.\n" + "다음에 또 만나요!";
        String url = "/main";

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName()+".kakaoLogout End!");
        return "/redirect";
    }

}