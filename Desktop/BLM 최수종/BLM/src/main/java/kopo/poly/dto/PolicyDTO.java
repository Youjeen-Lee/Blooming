package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PolicyDTO {

    private String pageIndex;
    private String totalCnt;
    private List<ApiResultDTO> youthPolicy;           // 실질적인 데이터 담고 있는 애가 얘, 얘가 하위태그 싹 묶어서 100개를 가지고 있음
                                                      // DTO를 변수로 선언해서 이 DTO가 이 클래스에 종속되도록

    public PolicyDTO() {
        // 기본 생성자
    }

}
