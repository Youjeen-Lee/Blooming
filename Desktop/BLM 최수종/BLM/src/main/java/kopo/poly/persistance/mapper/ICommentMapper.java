package kopo.poly.persistance.mapper;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ICommentMapper {

    // 내 댓글 가져오기
    List<CommentDTO> getMyCommentList(UserInfoDTO pDTO) throws Exception;

    //댓글 리스트
    List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception;

    //댓글 등록
    void insertCommentInfo(CommentDTO pDTO) throws Exception;


    //댓글 수정
    void updateCommentInfo(CommentDTO pDTO) throws Exception;

    //댓글 삭제
    void deleteCommentInfo(CommentDTO pDTO) throws Exception;

}
