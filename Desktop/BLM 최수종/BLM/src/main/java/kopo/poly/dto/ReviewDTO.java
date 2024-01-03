package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * lombok은 코딩을 줄이기 위해 @어노테이션을 통한 자동 코드 완성기능임
 *
 * @Getter => getter 함수를 작성하지 않았지만, 자동 생성
 * @Setter => setter 함수를 작성하지 않았지만, 자동 생성
 */
@Getter
@Setter
public class ReviewDTO { //댓글 하나에 대한 정보가 모아져있음

    private String review_seq; // 기본키, 순번
    private String store_seq; //fk
    private String user_id; // fk
    private String contents; // 글 내용
    private String reg_dt;


}

