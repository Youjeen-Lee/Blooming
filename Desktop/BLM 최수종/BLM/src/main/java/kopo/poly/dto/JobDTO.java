package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JobDTO {

    private int job_seq;

    // Job
    private String company_detail_name; // 기업명 company -> detail -> name
    private String company_detail_href; // 기업 주소 company -> detail -> href
    private String position_title; // position -> 공고 제목 title
    private String position_jobMidCode_name; // 업종명 job-mid-code -> name
    private String position_jobMidCode_Code; // 업종코드
    private String position_location_name; // 지역명 location -> name
                                           // 지역 분류가 구까지 한꺼번에 옴 우짬...코드도 지역명도 매한가지
    private String position_jobType_name; // job-type -> name (ex: "정규직")
    private String position_experienceLevel_name; // 경력 experience-level -> name (ex: "신입")
    private String position_requiredEducationLevel_name; // 학력 required-education-level -> name (ex: "학력무관")
    private String salary_name; // 연봉 salary -> name
    private String expirationDate; // 마감일의 Unix timestamp

    private String reg_dt;
    private String jobCode;
    private String areaCode;


}
