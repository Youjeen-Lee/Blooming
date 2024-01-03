package kopo.poly.controller;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserInfoController {

    private final IUserInfoService userInfoService;

//    /** 메인화면 매핑 **/
//    @GetMapping(value = "/main")
//    public String main() {
//        log.info(getClass().getName() + ".index화면 맵핑 시작");
//        log.info(getClass().getName() + ".index화면 맵핑 끝");
//
//        return "/main" ;
//    }



    /**
     * 회원가입 화면 매핑
     */
    @GetMapping(value = "/user/signUp")  //url 주소
    public String signUp() {
        log.info(getClass().getName() + ".회원가입화면 맵핑 시작");
        log.info(getClass().getName() + ".회원가입화면 맵핑 끝");

        return "/user/signUp" ;
    }

    /**
     * 회원가입 전 아이디 중복 조회
     */
    @ResponseBody // getUserIdExists()가 반환하는 rDTO 객체를 http 응답 본문으로 사용
    @PostMapping(value = "/user/getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists 시작!");

        // 1. url에서 파라미터값을 요청하여 user_id 변수에 담음
        // ==> 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
        String user_id = CmmUtil.nvl(request.getParameter("user_id"));

        log.info("user_id : " + user_id);

        // 2. UserInfoDTO 클래스 인스턴스 생성하여 pDTO라는 변수에 할당, pDTO 변수를 통해 UserInfoDTO 객체의 필드에 접근
        UserInfoDTO pDTO = new UserInfoDTO();
        // 3.pDTO(UserInfoDTO())의 여러 객체 중 'user_id'객체에 변수 user_id의 값을 set함
        pDTO.setUser_id(user_id);

        // 4. 사용자가 입력한 user_id 값을 가진 pDTO 값을 담아 아이디 중복확인하는 서비스 호출하고 널처리한 값을 rDTO 객체에 다음
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists 끝!");

        return rDTO;
    }

    /**
     * 회원 가입 전 이메일 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     * 유효한 이메일인 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     */
    @ResponseBody
    @PostMapping(value = "/user/getUserEmailExists")
    public UserInfoDTO getUserEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".이메일 중복체크 시작!");

        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 회원아이디

        log.info("user_email : " + user_email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        // 입력된 이메일이 중복된 이메일인지 조회
//        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        UserInfoDTO rDTO = userInfoService.getUserEmailExists(pDTO);

        if (rDTO == null) {
            log.info("rDTO가 널이라서 강제로 메모리에 올림");
            rDTO = new UserInfoDTO();
        }

        log.info(this.getClass().getName() + ".이메일 중복체크 끝!");

        return rDTO;
    }

    /**
     * 회원가입 로직 처리
     */
    @PostMapping(value = "/user/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception {
        // request는 url 요청으로부터 넘어온 값을 키를 통해 꺼내고, ModelMap은 결과 페이지에 데이터를 넘겨줌

        log.info(this.getClass().getName() + ".insertUserInfo 시작!");

        int res; // 가입 성공 여부 변수
        String msg = ""; //회원가입 결과에 대한 메시지를 전달할 변수
        String url = ""; //회원가입 결과에 대한  URL을 전달할 변수

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {
            // 제일 먼저 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함

            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            // 스프링에서 지원하는 request 객체의 getParameter 함수가 html form 태그의 input 태그의 name값을 키 값으로 가져옴
            // Cmmutil에 정의된 nvl 함수로 null 처리함
            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); //비밀번호
            String user_name = CmmUtil.nvl(request.getParameter("user_name")); //이름
//            String user_phone = CmmUtil.nvl(request.getParameter("user_phone")); //폰번호
            String user_email = CmmUtil.nvl(request.getParameter("user_email")); //이메일
            String user_nick = CmmUtil.nvl(request.getParameter("user_nick")); //닉네임
            String user_addr1 = CmmUtil.nvl(request.getParameter("user_addr1")); //주소
            String user_addr2 = CmmUtil.nvl(request.getParameter("user_addr2")); //상세주소


            // 로그 찍어서 값이 들어왔는지 확인
            log.info("user_id : " + user_id);
            log.info("user_pwd : " + user_pwd);
            log.info("user_name : " + user_name);
//            log.info("user_phone : " + user_phone);
            log.info("user_email : " + user_email);
            log.info("user_nick : " + user_nick);
            log.info("user_addr1 : " + user_addr1);
            log.info("user_addr2 : " + user_addr2);

            // 웹에서 받은 정보를 DTO에 저장하기 시작!! 웹으로 받은 정보는 DTO에 저장해야 스프링부트 내에서 정보를 주고받을 수 있다.

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO(); // UserInfoDTO 객체를 생성해서 pDTO라는 변수에 담음

            pDTO.setUser_id(user_id); // 화면으로부터 읽어온 (user_id)를 setUser_id 함수로 pDTO 객체에 집어넣음
            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));
            pDTO.setUser_name(user_name);
//            pDTO.setUser_phone(user_phone);
            //민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));
            pDTO.setUser_nick(user_nick);
            pDTO.setUser_addr1(user_addr1);
            pDTO.setUser_addr2(user_addr2);

            // 회원가입 서비스 호출
            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if (user_id.isEmpty() || user_pwd.isEmpty() || user_id.isEmpty() ||user_email.isEmpty() || user_addr1.isEmpty() || user_addr2.isEmpty()) {
                throw new Exception("필수 정보가 누락되었습니다.");
            }

            if (res == 1) {
                msg = "회원가입되었습니다.";
                url = "/user/login";
                // 추후 회원가입 입력화면에서 ajax를 활용해서 아이디 중복, 이메일 중복을 체크하길 바람
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
                url = "/user/signUp";
            }
        }
        catch (DuplicateKeyException e){ //PK인 USER_ID가 중복되어 에러가 발생했다면
            msg = "이미 가입된 아이디입니다. 다른 아이디로 변경 후 다시 시도해주세요.";
            url = "/user/signUp";
            log.info(e.toString());
            e.printStackTrace();
        }
        catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "\n공백 또는 유효하지않은 입력값입니다.\n\n다시 작성해주세요.";
            url = "/user/signUp";
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info("출력할 메세지 : "+ msg);
            log.info("이동할 경로 : "+ url);
            model.addAttribute("msg", msg);
            // 실제 전송할 메세지를 키값인 "msg"을 통해 밸류값 msg를 다음 페이지로 전달함
            model.addAttribute("url", url);

            log.info(this.getClass().getName() + ".insertUserInfo 끝!");
        }

        return "/redirect";
        // return "다음으로 보여줄 페이지경로"
        // 페이지 이동과 팝업창을 띄우기 위해 msg와 url을 redirect로 보내줌.

    }

    /**
     * 로그인을 위한 입력 화면으로 이동
     */
    @GetMapping(value = "/user/login")
    public String login() {
        log.info(this.getClass().getName() + "로그인 시작!");
        log.info(this.getClass().getName() + "로그인 끝!");
        return "/user/login";
    }


    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     */
    @PostMapping(value = "/user/loginProc")
    public String loginProc(HttpServletRequest request, ModelMap model, HttpSession session) {

        log.info(this.getClass().getName() + ".loginProc Start!");

        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        String url = "";
        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); //비밀번호

            log.info("user_id : " + user_id);
            log.info("user_pwd : " + user_pwd);

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));
            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);
            /*
             * 로그인을 성공했다면, 회원아이디 정보를 session에 저장함
             *
             * 세션은 톰켓(was)의 메모리에 존재하며, 웹사이트에 접속한 사람(연결된 객체)마다 메모리에 값을 올린다.

             * 스프링에서 세션을 사용하기 위해서는 함수명의 파라미터에 HttpSession session 존재해야 한다.
             * 세션은 톰켓의 메모리에 저장되기 때문에 url마다 전달하는게 필요하지 않고,
             * 그냥 메모리에서 부르면 되기 때문에 화면, controller에서 쉽게 불러서 쓸수 있다.
             * */
            if (CmmUtil.nvl(rDTO.getUser_id()).length() > 0) { //로그인 성공
                /*
                 * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
                 * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
                 *
                 * Session 단어에서 SS를 가져온 것이다.
                 */
                session.setAttribute("SS_USER_ID", user_id);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUser_name()));

                //로그인 성공 메세지와 이동할 경로의 url
                msg = "로그인 성공!\n" + "블루밍과 함께 좋은 하루되세요.";
                url = "/main";

            } else {
                msg = "아이디와 비밀번호를 올바르게 입력해주세요.";
                url = "/user/login";
            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            log.info(this.getClass().getName() + ".loginProc End!");
        }

        return "/redirect";
    }
    /** 로그아웃 */
    @GetMapping(value = "/user/logout")
        public String logoutProc (ModelMap model, HttpSession session) {

            log.info(this.getClass().getName() + ".logoutProc Start!");

            session.invalidate();

            String msg = "로그아웃 되셨습니다.\n" + "다음에 또 만나요!";
            String url = "/main";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);


           return "/redirect";
        }

    /**
     * 아이디 찾기 화면
     */
    @GetMapping(value = "/user/searchUserId")
    public String findId() {
        log.info(this.getClass().getName() + ".searchUserId Start!");

        log.info(this.getClass().getName() + ".searchUserId End!");

        return "/user/searchUserId";

    }

    /**
     * 아이디 찾기 로직 수행
     */
    @PostMapping(value = "/user/searchUserIdProc")
    public String searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + "searchUserIdProc Start!");
        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장!!
         *
         *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
         * ########################################################################
         */

        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 이메일
        /*
         * ########################################################################
         * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
         * 						반드시 작성할 것
         * ########################################################################
         * */

        log.info("user_email : " + user_email);
        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기!!
         *
         *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
         * ########################################################################
         */


        UserInfoDTO pDTO = new UserInfoDTO();
//        pDTO.setUser_name(user_name);
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserId(pDTO))
                .orElseGet(UserInfoDTO::new);

        String user_id = CmmUtil.nvl(rDTO.getUser_id());

        String msg = ""; //아이디 찾기 결과에 대한 메시지를 전달할 변수, 변수 선언을 안하면 데이터를 쓸 수가 없음
        String url = "";


        log.info("user_id : " + rDTO.getUser_id());




        if(user_id.length() != 0) {
            msg = "회원님의 아이디는 " + rDTO.getUser_id() + " 입니다.";
            url = "/user/searchUserPwd";
        } else {
            msg = "아이디를 찾을 수 없습니다.";
            url = "/user/searchUserId";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName() + "searchUserIdProc End!");

        return "/redirect";
    }


    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "/user/searchUserPwd")
    public String findPwd(HttpSession session) {
        log.info(this.getClass().getName() + ".searchUserPwd Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_USER_PWD 세션 삭제
        session.setAttribute("NEW_USER_PWD", "");
        session.removeAttribute("NEW_USER_PWD");

        log.info(this.getClass().getName() + ".searchUserPwd End!");

        return "/user/searchUserPwd";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "/user/searchUserPwdProc")
    public String searchUserPwdProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".searchUserPwdProc Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id")); // 아이디
        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 이메일

        log.info("user_id : " + user_id);
        log.info("user_email : " + user_email);


        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id); // 아이디가 pk값이므로 아이디만 일치하면 비밀번호 찾기 가능하므로 id값만 set한다.
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        // 아이디 있는지 조회하고 있으면 임시 비번 발송
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserPwd(pDTO)).orElseGet(UserInfoDTO::new);

        String user_pwd = CmmUtil.nvl(String.valueOf(rDTO.getAuth_number())); //int인 authnumber를 string으로 바꾼 후 비밀번호에 넣음

        log.info("user_pwd : " + user_pwd);

        String msg = ""; //비밀번호 찾기 결과에 대한 메시지를 전달할 변수, 변수 선언을 안하면 데이터를 쓸 수가 없음
        String url = "";

        if(user_pwd.length() == 8) { // 인증번호가 정상적으로 발송됐다면 8자리이므로
            msg = "회원님의 이메일로 임시 비밀번호를 전송했습니다.";
            url = "/user/changeUserPwd";
        } else {
            msg = "일치하는 정보가 없습니다.";
            url = "/user/searchUserPwd";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);


        log.info("임시비번을 새 비밀번호로 변경 시작");

        UserInfoDTO userInfoDTO = new UserInfoDTO();

        userInfoDTO.setUser_id(user_id);
        userInfoDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd)); //db에 암호화된 비번이 저장되어있으므로 암호화 시켜줌

        userInfoService.changeUserPwd(userInfoDTO);

        log.info("임시비밀번호를 새로운 비밀번호로 변경 완료");

        // 비밀번호 재생성하는 화면은 보안을 위해 반드시 NEW_USER_PWD 세션이 존재해야 접속 가능하도록 구현
        // user_id 값을 넣은 이유는 비밀번호 재설정하는 newUserPwdProc 함수에서 사용하기 위함
        // 왜? findPwd의 과정을 거치지않은채 비밀번호를 변경할수도 있으므로 인증을 받은 사용자만 비밀번호를 바꿀 수 있도록 하기위해

        session.setAttribute("NEW_USER_ID", user_id);
        // 비밀번호를 변경하고자하는 회원의 아이디를 세션에 등록
        // 왜? 비밀번호 최종 수정할 때 사용자가 입력한 비밀번호를  대조해서, 실제 메일로 발송된 인증번호와 일치하는지 확인하고,
        // 최종 비밀번호 업데이트 쿼리에도 사용됨 (비밀번호를 변경할 때, 대상 아이디를 지정해서 변경해야 하기 때문)

        log.info(this.getClass().getName() + ".searchUserPwdProc End!");

        return "/redirect";

    }

    /**
     * 비밀번호 바꾸기 화면
     */
    @GetMapping(value = "/user/changeUserPwd")
    public String changePwd() {
        log.info(this.getClass().getName() + ".changeUserPwd Start!");

        log.info(this.getClass().getName() + ".changeUserPwd End!");

        return "/user/changeUserPwd";

    }

    /**
     * 비밀번호 바꾸기 수행
     */
    @PostMapping(value ="/user/changeUserPwdProc")
    public String newUserPwdProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + "changeUserPwdProc Start!");

        String msg = ""; // 웹에 보여줄 메시지
        String url = ""; // 이동할 url


        // 정상적인 접근인지 체크, 비밀번호 업데이트하려는 사용자의 아이디를 세션에서 불러옴
        String new_user_id = CmmUtil.nvl((String) session.getAttribute("NEW_USER_ID"));
        log.info("user_id : " + new_user_id);

        if (new_user_id.length() > 0) { //정상 접근

            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); // 기존 비밀번호
            log.info("입력받은 임시 비밀번호 : " + user_pwd);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(new_user_id);
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));


            // 임시 비번과 세션에 저장된 아이디로 로그인 로직 수행
            UserInfoDTO userInfoDTO = userInfoService.getLogin(pDTO);

            if(userInfoDTO.getUser_id().length() !=0) {
                log.info("비밀번호가 일치합니다.");

                String new_user_pwd = CmmUtil.nvl(request.getParameter("new_user_pwd")); // 신규 비밀번호
                log.info("입력받은 신규 비밀번호 : " + new_user_pwd);

                pDTO.setUser_pwd(EncryptUtil.encHashSHA256(new_user_pwd)); // 신규 비밀번호 암호화 set

                userInfoService.changeUserPwd(pDTO);

                // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_USER_ID 세션 삭제
                session.setAttribute("NEW_USER_ID", "");
                session.removeAttribute("NEW_USER_ID");

                msg = " 비밀번호가 변경되었습니다.\n 로그인 페이지로 이동합니다.";
                url = "/user/login";

            } else { // 비밀번호가 일치하지 않을 때
                msg = "기존 비밀번호와 일치하지 않습니다.";
                url = "/user/changeUserPwd";
            }


        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
            url = "/user/changeUserPwd";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName() + "비밀번호 바꾸기 End!");

        return "/redirect";

    }

    @GetMapping(value="/user/deleteUser")
    public String deleteUser(ModelMap modelMap, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + "deleteUser Start!");

        String user_id = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
        int res = 0;

        log.info("user_id : " + user_id);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id);

        res = userInfoService.deleteUser(pDTO);

        String msg = ""; // 아이디 찾기 결과에 대한 메세지를 전달할 변수, 변수 선언을 안 하면 데이터를 쓸 수가 없음
        String url = "";

        if(res == 1) {
            msg = "회원탈퇴 되었습니다.";
            url = "/main";
            session.removeAttribute("SS_USER_ID");
        } else {
            msg = "회원탈퇴에 실패하였습니다.";
        }

        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);

        log.info(this.getClass().getName() + "deleteUser End!");

        return "/redirect";
    }





}








