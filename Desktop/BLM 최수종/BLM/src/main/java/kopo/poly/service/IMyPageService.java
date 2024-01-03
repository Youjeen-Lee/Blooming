package kopo.poly.service;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IMyPageService {

    // 내 작성글 가져오기
    List<NoticeDTO> getMyNoticeList(UserInfoDTO pDTO) throws Exception;
    // 내 작성댓글 가져오기
    List<CommentDTO> getMyCommentList(UserInfoDTO pDTO) throws Exception;

    // 회원정보 수정할 때 가입자 정보 가져오기
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원정보 수정
    int updateUserInfo(UserInfoDTO pDTO) throws Exception;
}
