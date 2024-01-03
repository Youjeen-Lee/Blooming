package kopo.poly.service.impl;

import kopo.poly.dto.MailDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    private final IUserInfoMapper userInfoMapper; // 회원관련 SQL 사용하기 위한 Mapper 가져오기, 모즌 서비스에서 매퍼를 쓸 수 있어야하니까 전역변수로 선언
    private final IMailService mailService; // mailService 쓰기위해서


    // 로그인 위해 아이디와 비밀번호 일치 확인
    @Override
    public UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLogin Start!");

        // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 mapper 호출하기
        // userInfoMapper.getUserLoginCheck(pDTO) 함수 실행 결과가 NUll 발생하면, UserInfoDTO 메모리에 올리기
        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getLogin(pDTO)).orElseGet(UserInfoDTO::new);

        // 비밀번호가 틀려서 로그인이 실패하여 결과가 null인 채로 결과가 돌아오면 널포인트익셉션 에러가 뜨는걸 방지하기위해 작성
        if(rDTO == null) {
            rDTO = new UserInfoDTO();
            log.info("로그인 실패!!");
        }

        /*
         * userInfoMapper로 부터 SELECT 쿼리의 결과로 회원아이디를 받아왔다면, 로그인 성공!!
         *
         * DTO의 변수에 값이 있는지 확인하기 처리속도 측면에서 가장 좋은 방법은 변수의 길이를 가져오는 것이다.
         * 따라서  .length() 함수를 통해 회원아이디의 글자수를 가져와 0보다 큰지 비교한다.
         * 0보다 크다면, 글자가 존재하는 것이기 때문에 값이 존재한다.
         */
        if (CmmUtil.nvl(rDTO.getUser_id()).length() > 0) {

            log.info("로그인 성공");
        }

        log.info(this.getClass().getName() + ".getLogin End!");

        return rDTO;
    }



    // 회원가입 아이디 중복 조회
    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO = userInfoMapper.getUserIdExists(pDTO);

        String exists_yn = CmmUtil.nvl(rDTO.getExists_yn());
        log.info("exists_yn : " + exists_yn);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    //회원가입 이메일 중복 확인
    @Override
    public UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".이메일 중복조회 Start!");

        log.info("pDTO user_email : "+ pDTO.getUser_email());
        // DB 이메일이 존재하는지 SQL 쿼리 실행
        // SQL 쿼리에 COUNT()를 사용하기 때문에 반드시 조회 결과는 존재함
        UserInfoDTO rDTO = userInfoMapper.getUserEmailExists(pDTO);
        if (rDTO == null) {
            rDTO = new UserInfoDTO();
        }

        String exists_yn = CmmUtil.nvl(rDTO.getExists_yn());

        log.info("exists_yn : " + exists_yn);

        if (exists_yn.equals("N")) {
            // 6자리 랜덤 숫자 생성하기
            int authNumber = ThreadLocalRandom.current().nextInt(100000,1000000);

            log.info("authNumber : " + authNumber);

            // 인증번호 발송 로직
            MailDTO dto = new MailDTO();

            dto.setTitle("블루밍 이메일 중복 확인 인증 번호 발송");
            dto.setContents("인증번호는 " + authNumber + " 입니다.");
            dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getUser_email())));

            mailService.doSendMail(dto);

            dto = null;

            rDTO.setAuth_number(authNumber); // 인증번호를 결과값에 넣어주기

        }

        log.info(this.getClass().getName() + ".user_emailAuth End!");

        return rDTO;
    }

    // 회원가입 정보 등록
    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        // 회원가입 성공 : 1, 기타 에러 발생 : 0
        int res = 0;

        // 회원가입
        res = userInfoMapper.insertUserInfo(pDTO);


        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return res;
    }

    // 아이디찾기(결과는 alert)
    @Override
    public UserInfoDTO searchUserId(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".searchUserId Start!");

        UserInfoDTO rDTO = userInfoMapper.searchUserId(pDTO);

        log.info(this.getClass().getName() + ".searchUserId End!");

        return rDTO;
    }



    // 비밀번호 찾기(인증번호 메일로 보냄)

    @Override
    public UserInfoDTO searchUserPwd(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "일치하는 아이디 찾기 Start!");

        log.info("pdto user_id : "+pDTO.getUser_id());

        // DB 아이다가 존재하는지 SQL 쿼리 실행
        // SQL 쿼리에 COUNT()를 사용하기 때문에 반드시 조회 결과는 존재함
        UserInfoDTO rDTO = userInfoMapper.checkUserIdAndEmail(pDTO);

        if (rDTO == null) {
            rDTO = new UserInfoDTO();
            rDTO.setAuth_number(0); // 컨트롤러에서 authnumber 꺼낼 때 널포이트익셉션 방지하기 위해 억지로 값 주입
            log.info("아이디와 이메일이 일치하지않음.");

        } else {
            // 8자리 랜덤 숫자 생성하기
            int authNumber = ThreadLocalRandom.current().nextInt(10000000,100000000);

            log.info("authNumber : " + authNumber);

            // 인증번호 발송 로직
            MailDTO dto = new MailDTO();

            dto.setTitle("블루밍 임시 비밀번호 발송");
            dto.setContents("임시 비밀번호는 " + authNumber + " 입니다.");
            dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getUser_email()))); // 메일 보낼 때는 암호화 풀어서 보내야함

            log.info(this.getClass().getName() + ".임시 비밀번호 보내기!");
            mailService.doSendMail(dto);

            dto = null; //임시 비번이 메모리에 남지않게 지워줌

            rDTO.setAuth_number(authNumber); // 인증번호를 결과값에 넣어주기

        }
        log.info(this.getClass().getName() + "일치하는 아이디 찾기 End!");

        return rDTO;
    }


    // 비밀번호 재설정
    @Override
    public int changeUserPwd(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".changeUserPwdProc Start!");

        // 비밀번호 재설정
        int success = userInfoMapper.updateUserPwd(pDTO);

        log.info(this.getClass().getName() + ".changeUserPwdProc End!");

        return success;
    }

    @Override
    public int deleteUser(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "deleteUser Start!");

        int res = 0;

        res = userInfoMapper.deleteUser(pDTO);

        log.info(this.getClass().getName() + "deleteUser End!");

        return res;
    }

}
