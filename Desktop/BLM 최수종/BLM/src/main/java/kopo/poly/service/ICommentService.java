package kopo.poly.service;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;

import java.util.List;


public interface ICommentService {
    /**
     * 댓글 리스트
     * @return 조회 경과
     */
    List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception;

    /**
     * 댓글 등록
     * @param pDTO 화면에서 입력된 공지사항 입력된 값들
     */
    void insertCommentInfo(CommentDTO pDTO) throws Exception;

    /**
     * 댓글 수정
     * @param pDTO 화면에서 입력된 수정되기 위한 공지사항 입력된 값들
     */
    void updateCommentInfo(CommentDTO pDTO) throws Exception;

    /**
     * 댓글 삭제
     *
     * @param pDTO 삭제할 notice_seq 값
     */
    void deleteCommentInfo(CommentDTO pDTO) throws Exception;

}

