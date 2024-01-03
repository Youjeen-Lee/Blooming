package kopo.poly.persistance.mapper;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.ReviewDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IReviewMapper {

    // 내 리뷰 가져오기
    List<ReviewDTO> getMyReviewList(UserInfoDTO pDTO) throws Exception;

    //리뷰 리스트
    List<ReviewDTO>getReviewList(ReviewDTO pDTO) throws Exception;

    //리뷰 등록
    void insertReviewInfo(ReviewDTO pDTO) throws Exception;


    //리뷰 수정
    void updateReviewInfo(ReviewDTO pDTO) throws Exception;

    //리뷰 삭제
    void deleteReviewInfo(ReviewDTO pDTO) throws Exception;

}
