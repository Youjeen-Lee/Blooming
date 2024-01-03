package kopo.poly.controller;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IMyPageService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final IMyPageService MyPageService;

    @GetMapping(value = "myPageHome")
    public String myPage() throws Exception {

        log.info(this.getClass().getName() + "블루밍 마이페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "블루밍 마이페이지로 이동 컨트롤러 종료");

        return "/myPage/myPageHome";
    }

    @GetMapping(value = "myNoticeList")
    public String getMyNoticeList(HttpSession session, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + "내 작성글 페이지로 이동 컨트롤러 실행");

        String url = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);

            List<NoticeDTO> rList = MyPageService.getMyNoticeList(pDTO);

            if (rList.size() < 1) {
                url = "/myPage/notPosted";
            } else {
                url = "/myPage/myNoticeList";
            }

            modelMap.addAttribute("rList", rList);
        } catch (Exception e) {

            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + "내 작성글 페이지로 이동 컨트롤러 종료");
        }

        return url;
    }

    @GetMapping(value = "myCommentList")
    public String getMyCommentList(HttpSession session, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + "내 작성댓글 페이지로 이동 컨트롤러 실행");

        String url = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);

            List<CommentDTO> rList = MyPageService.getMyCommentList(pDTO);

            if (rList.size() < 1) {
                url = "/myPage/notPosted";
            } else {
                url = "/myPage/myCommentList";
            }

            modelMap.addAttribute("rList", rList);
        } catch (Exception e) {

            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + "내 작성댓글 페이지로 이동 컨트롤러 종료");
        }

        return url;
    }

    @GetMapping(value = "editUserInfo")
    public String editUserInfo(HttpSession session, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".회원정보 수정 페이지로 이동합니다.");

        String alert = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            String scLogin = (String) session.getAttribute("ACCESS_KIND"); // 접근 종류
            if(scLogin == "KAKAO" || scLogin == "NAVER") {
                alert = "소셜로그인 회원은 정보수정이 불가합니다.";
            }

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);

            UserInfoDTO rDTO = MyPageService.getUserInfo(pDTO);
            rDTO.setUser_email(EncryptUtil.decAES128CBC(rDTO.getUser_email())); // 이메일은 화면에 띄워야 하니까 복호화해서 다시 DTO에 담아야 함

            modelMap.addAttribute("rDTO", rDTO);
            modelMap.addAttribute("alert", alert);
        } catch (Exception e) {

            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".회원정보 수정 페이지로 이동 종료합니다.");
        }

        return "/myPage/editUserInfo";
    }

    @PostMapping(value = "updateUserInfo")
    public String updateUserInfo(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".컨트롤러 회원정보 수정 작업 요청 받았습니다.");

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String user_addr1 = CmmUtil.nvl(request.getParameter("user_addr1"));
            String user_addr2 = CmmUtil.nvl(request.getParameter("user_addr2"));

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);
            pDTO.setUser_addr1(user_addr1);
            pDTO.setUser_addr2(user_addr2);

            int r = MyPageService.updateUserInfo(pDTO);

            if (r == 1) {
                log.info(user_id + " 님의 정보가 수정되었습니다.");
            }
        } catch (Exception e) {

            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".컨트롤러 회원정보 수정 작업 요청 종료합니다.");
        }

        return "/myPage/myPageHome";
    }

    @GetMapping(value = "FAQ")
    public String FAQ() throws Exception {
        log.info(this.getClass().getName() + "자주 묻는 질문 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "자주 묻는 질문 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq";
    }

    @GetMapping(value = "FAQ_1")
    public String FAQ_1() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_1";
    }

    @GetMapping(value = "FAQ_2")
    public String FAQ_2() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_2";
    }

    @GetMapping(value = "FAQ_3")
    public String FAQ_3() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_3";
    }

    @GetMapping(value = "FAQ_4")
    public String FAQ_4() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_4";
    }

    @GetMapping(value = "FAQ_5")
    public String FAQ_5() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_5";
    }

    @GetMapping(value = "FAQ_6")
    public String FAQ_6() throws Exception {
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 실행");
        log.info(this.getClass().getName() + "질문 상세보기 페이지로 이동 컨트롤러 종료");

        return "/myPage/faq_6";
    }

}
