package kopo.poly.service.impl;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.ICommentMapper;
import kopo.poly.persistance.mapper.INoticeMapper;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IMyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService implements IMyPageService {

    private final INoticeMapper noticeMapper;
    private final ICommentMapper commentMapper;
    private final IUserInfoMapper userInfoMapper;

    @Override
    public List<NoticeDTO> getMyNoticeList(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "내 작성글 가져오기 서비스 호출 받았습니다.");

        List<NoticeDTO>rList = noticeMapper.getMyNoticeList(pDTO);

        log.info(this.getClass().getName() + "내 작성글 가져오기 서비스 호출 종료합니다.");

        return rList;
    }

    @Override
    public List<CommentDTO> getMyCommentList(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "내 댓글 가져오기 서비스 호출 받았습니다.");

        List<CommentDTO>rList = commentMapper.getMyCommentList(pDTO);

        log.info(this.getClass().getName() + "내 댓글 가져오기 서비스 호출 종료합니다.");

        return rList;
    }

    @Override
    public UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "가입자 정보 매퍼로 호출합니다.");

        UserInfoDTO rDTO = userInfoMapper.getUserInfo(pDTO);

        log.info(this.getClass().getName() + "가입자 정보 매퍼로 호출 종료합니다.");

        return rDTO;
    }

    @Override
    public int updateUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "매퍼로 회원정보 수정 요청합니다.");
        log.info(this.getClass().getName() + "매퍼로 회원정보 수정 요청 종료합니다.");

        return userInfoMapper.updateUserInfo(pDTO);
    }
}
