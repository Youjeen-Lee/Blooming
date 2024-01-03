package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    // 회원정보 수정할 때 가입자 정보 가져오기
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원정보 수정
    int updateUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기(회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception; //반환타입 적어줄것

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 아이디 중복체크하기(DB조회하기)
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 이메일 중복체크하기(DB조회하기)
    UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception;

    // 아이디 찾기
    UserInfoDTO searchUserId(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 찾기에서 아이디와 이메일 일치하는지
    UserInfoDTO checkUserIdAndEmail(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int updateUserPwd(UserInfoDTO pDTO) throws Exception;

    // 회원 탈퇴
    int deleteUser(UserInfoDTO pDTO) throws Exception;


}
