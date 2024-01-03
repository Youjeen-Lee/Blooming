package kopo.poly.persistance.mapper;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.JobDTO;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IJobMapper {

    // 테이블 비우기
    int truncateJobInfo() throws Exception;

    // select count(*) from 테이블명 해주는 함수 만들어서 돌려보기
    int getCountTable() throws Exception;

    // 정책 db에 넣기
    int insertJobInfo(JobDTO pDTO) throws Exception;

    // 정책 리스트 가져오기(정책 메인)
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
