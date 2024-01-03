package kopo.poly.service;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.JobDTO;

import java.util.List;

public interface IJobService {

    String apiURL = "https://oapi.saramin.co.kr/job-search";

    // 일자리 API를 호출하여 일자리 결과 받아오기
    int insertJobList() throws Exception;

    // 일자리 메인 페이지 진입
    List<JobDTO> getJobList() throws Exception;

    // 메인 블럭에 띄울 최신 일자리 10개
    List<JobDTO> getMainJobList() throws Exception;


    // 정책 게시물 상세보기
    JobDTO getJobInfo(JobDTO pDTO) throws Exception;

    // 지역별 일자리 가져오기
    List<JobDTO> getCountAreaList(JobDTO pDTO) throws Exception;

    // 업종별 일자리 가져오기
    List<JobDTO> getCountCategoryList(JobDTO pDTO) throws Exception;

    // 지역 + 업종별 일자리 가져오기
    List<JobDTO> getCountOptionList(JobDTO pDTO) throws Exception;


}

