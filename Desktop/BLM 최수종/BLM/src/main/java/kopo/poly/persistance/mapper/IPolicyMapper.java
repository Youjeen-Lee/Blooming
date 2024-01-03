package kopo.poly.persistance.mapper;

import kopo.poly.dto.ApiResultDTO;

import kopo.poly.dto.PolicyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IPolicyMapper {

    // 테이블 비우기
    int truncatePolicyInfo() throws Exception;
    // 데이터베이스 카운트
    int checkPolicyData() throws Exception;
    // 정책 api 호출 후 데이터베이스에 저장
    int insertPolicyInfo(ApiResultDTO pDTO) throws Exception;
    // 정책 리스트 가져오기(정책 게시판 진입)
    List<ApiResultDTO> getCountTable() throws Exception;
    // 조건별 정책 리스트 가져오기 1, 2, 3
    List<ApiResultDTO> getCountAreaList(ApiResultDTO pDTO) throws Exception;
    List<ApiResultDTO> getCountCategoryList(ApiResultDTO pDTO) throws Exception;
    List<ApiResultDTO> getCountOptionList(ApiResultDTO pDTO) throws Exception;
    // 메인 블럭에 띄울 최신 정책 10개
    List<ApiResultDTO> getMainPolicyList() throws Exception;

    // 정책 게시물 상세보기
    ApiResultDTO getPolicyInfo(ApiResultDTO pDTO) throws Exception;

}
