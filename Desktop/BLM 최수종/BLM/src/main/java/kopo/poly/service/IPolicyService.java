package kopo.poly.service;

import kopo.poly.dto.ApiResultDTO;

import java.util.List;

public interface IPolicyService {

    // API 호출할 때 앞에 붙는 기본 url
    String apiURL = "https://www.youthcenter.go.kr/opi/youthPlcyList.do";

    // 정책 API 호출
    void getPolicyApi() throws Exception;

    // 메인 블럭에 띄울 최신 정책 10개
    List<ApiResultDTO> getMainPolicyList() throws Exception;

    // 정책 리스트 가져오기(정책 게시판 진입)
    List<ApiResultDTO> getCountTable() throws Exception;

    // 지역별 정책 가져오기
    List<ApiResultDTO> getCountAreaList(ApiResultDTO pDTO) throws Exception;

    // 분야별 정책 가져오기
    List<ApiResultDTO> getCountCategoryList(ApiResultDTO pDTO) throws Exception;

    // 지역 + 분야별 정책 가져오기
    List<ApiResultDTO> getCountOptionList(ApiResultDTO pDTO) throws Exception;

    // 정책 게시물 상세보기
    ApiResultDTO getPolicyInfo(ApiResultDTO pDTO) throws Exception;
}

