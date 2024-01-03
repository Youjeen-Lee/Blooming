package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

public interface IUserInfoService { //안의 함수들은 자동으로 private이므로 접근제어자 생략

    // 회원 가입하기(회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;
    // 아이디 중복 체크
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 주소 중복 체크 및 인증 번호 보내기
    UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception;


    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디 찾기
    UserInfoDTO searchUserId(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 찾기
    UserInfoDTO searchUserPwd(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int changeUserPwd(UserInfoDTO pDTO) throws Exception;

    // 회원 탈퇴
    int deleteUser(UserInfoDTO pDTO) throws Exception;

}
