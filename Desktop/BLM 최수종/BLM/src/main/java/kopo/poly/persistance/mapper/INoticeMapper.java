package kopo.poly.persistance.mapper;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface INoticeMapper {

    List<NoticeDTO> getMyNoticeList(UserInfoDTO pDTO) throws Exception;

    // 메인에 띄울 최신글 10개
    List<NoticeDTO> getMainNoticeList() throws Exception;

    // 최신순으로 정렬하기
    List<NoticeDTO> getSortByDate() throws Exception;

    // 조회수순으로 정렬하기
    List<NoticeDTO> getSortByViewCount() throws Exception;

    //게시판 리스트
    List<NoticeDTO> getNoticeList() throws Exception;

    // 지역별 커뮤니티글 가져오기
    List<NoticeDTO> getListByArea(NoticeDTO pDTO) throws Exception;

    //게시판 글 등록
    void insertNoticeInfo(NoticeDTO pDTO) throws Exception;

    //게시판 상세보기
    NoticeDTO getNoticeInfo(NoticeDTO pDTO) throws Exception;

    //게시판 조회수 업데이트
    void updateNoticeReadCnt(NoticeDTO pDTO) throws Exception;

    //게시판 글 수정
    void updateNoticeInfo(NoticeDTO pDTO) throws Exception;

    //게시판 글 삭제
    void deleteNoticeInfo(NoticeDTO pDTO) throws Exception;

}
