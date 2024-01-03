package kopo.poly.service;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.ReviewDTO;


import java.util.List;


public interface IReviewService {
    /**
     * 리뷰 리스트
     * @return 조회 경과
     */
    List<ReviewDTO> getReviewList(ReviewDTO pDTO) throws Exception;

    /**
     * 리뷰 등록
     * @param pDTO 화면에서 입력된 공지사항 입력된 값들
     */
    void insertReviewInfo(ReviewDTO pDTO) throws Exception;

    /**
     * 리뷰 수정
     * @param pDTO 화면에서 입력된 수정되기 위한 공지사항 입력된 값들
     */
    void updateReviewInfo(ReviewDTO pDTO) throws Exception;

    /**
     * 리뷰 삭제
     *
     * @param pDTO 삭제할 review_seq 값
     */
    void deleteReviewInfo(ReviewDTO pDTO) throws Exception;

}

