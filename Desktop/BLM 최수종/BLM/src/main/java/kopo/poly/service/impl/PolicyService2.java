package kopo.poly.service.impl;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.PolicyDTO;
import kopo.poly.persistance.mapper.IPolicyMapper;
import kopo.poly.service.IPolicyService;
import kopo.poly.util.CmmUtil;
import com.fasterxml.jackson.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService2 implements IPolicyService {
    private final IPolicyMapper policyMapper;

    @Value("${policy.api.key}")
    private String policyApiKey;


    // <<정책 API 호출하기>>
    @Scheduled(cron = "0 7 7 * * MON-FRI")
//    @Scheduled(fixedDelay = 999999999, initialDelay = 500)
    @Override
    public void getPolicyApi() throws Exception {
        log.info(this.getClass().getName() + "블루밍 서비스 정책 API 호출 실행합니다.");

        // 데이터베이스 비우기
        int z = policyMapper.truncatePolicyInfo();

        if (z == 1) {
            log.info("데이터베이스 비우기 완료");
        }

        // 1보다 작다는 건 비우는 작업이 실행되었다는 것
        int d = policyMapper.checkPolicyData();
        log.info("현재 DB에 저장된 데이터의 개수 : " + d);

        int display = 100;            // 출력건수(기본값 : 10, 최대 : 100까지)
        int pageIndex = 1;            // 조회할 페이지(기본값 : 1)
        String srchPolyBizSecd = "003002002";   // 지역코드(부산부터 시작)
        // 003002002 :부산 | 003002003 :대구 | 003002004 :인천 | 003002005 :광주 | 003002006 :대전 | 003002007 :울산 | 003002008 :경기 |
        // 003002009 :강원 | 003002010 :충북 | 003002011 :충남 | 003002012 :전북 | 003002013 :전남 | 003002014 :경북 | 003002015 :경남 |
        // 003002016 :제주 | 003002017 :세종

        // 호출한 지역코드 확인용 리스트
        ArrayList<String> areaParameter = new ArrayList<>();

        // API 호출 후 값 파싱 및 처리하는 반복문
        int i = 1;
        callAPI : while (i < 80) {

            log.info("현재 i : " + i);
            log.info(">>>>>>>>>>>>>>>>>>> 반복문 입구 >>>>>>>>>>>>>>>>>>>");

            // * 1. OpenAPI 호출을 위한 'URL 파라미터 만들기' --> URL은 ?로 연결하고 각각의 키와 값(한 쌍)은 &로 연결함(기본 url과 생성 url의 연결은 ?, 파라미터들의 연결은 &)
            String apiParam = "?openApiVlak=" + policyApiKey + "&display=" + display + "&pageIndex=" + pageIndex + "&srchPolyBizSecd=" + srchPolyBizSecd;
            // 기본 url 뒤에 붙일 파라미터들 로그로 확인
            log.info("파라미터로 들어가는 값들은 : " + apiParam + " 입니다.");
            log.info("현재 호출중인 지역코드는 : " + srchPolyBizSecd + "입니다.");

            // * 2. 호출할 'url 생성'
            // 위에서 만든 url요청값을 미리 상수로 정해둔 요청값(인터페이스에 정의해둔 것) 뒤에 붙여서 쓸 것 그리고 로그로 생성된 최종 url 확인
            URL url = new URL(IPolicyService.apiURL + apiParam);
            log.info(">>>>>>>>>>>>>>>>>>> url 생성 완료");
            log.info(url.toString());

            // * 3. URL '호출'
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            log.info(">>>>>>>>>>>>>>>>>>> url 요청 전달 완료");

            // * 4. 받아온 데이터(응답) '처리'
            InputStream inputStream = urlConnection.getInputStream();
            log.info(">>>>>>>>>>>>>>>>>>> 스트림으로 데이터 받아오기 완료");


            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();

            // 문자열 변수 하나 생성하고,
            String line;
            // "빈 문자열이 아니라면!" reader의 readerLine() 메소드를 사용하여 문자열을 한 줄 한 줄 읽어서 line이라는 변수에 추가하고 line이 갖고 있는 값을 response에 하나하나 추가할 것
            //  빈 문자열이 나와 읽을 게 없을 때까지 반복
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            log.info(">>>>>>>>>>>>>>>>>>> 버퍼로 데이터 전부 읽어서 변수에 추가하기 완료");


            // * 5. api 호출 후 받아온 데이터(xml)를 자바 객체로 변환

            // xml로 넘어온 결과를 문자열로 바꿔서 저장
            String resultXml = response.toString();
            log.info("api 호출 결과(시작 태그는 <youthPolicyList>) : " + resultXml);
            log.info(">>>>>>>>>>>>>>>>>>> 받아온 전체 결과 한 번에 문자열로 전환 완료");

            // Jackson 라이브러리가 제공하는 XmlMapper를 사용하여 xml을 자바 객체로 매핑할 것
            XmlMapper xmlMapper = new XmlMapper();

            log.info(">>>>>>>>>>>>>>>>>> xmlMapper의 readValue를 통해서 값 매핑 실행");

            List<PolicyDTO> resultData = xmlMapper.readValue(resultXml, new ArrayList<PolicyDTO>().getClass());

            log.info(">>>>>>>>>>>>>>>>>>> xmlMapper의 readValue 작업 완료");


            try {

                // 로그를 찍어보면 get(0)부터 태그 하나하나를 읽은 값이 순서대로 나오고 있음 애초에 받아오는 결과값부터가 리스트
                log.info("youthPolicy :" + resultData);
                // 102개가 나옴 왜냐 1, 2번째 태그와 youthPolicy 태그 100개 그래서 총 102개
                log.info("생성된 DTO의 개수 : " + resultData.size());
                log.info("첫 번째 DTO 출력( pageIndex, 현재 페이지는) : " + resultData.get(0) + "입니다.");
                log.info("API로부터 받아온 데이터의 총 개수(totalCnt, 두 번째 DTO, 출력 데이터의 개수는) : " + resultData.get(1) + "입니다.");
                log.info("DTO 내 <youthPolicy> 출력(youthPolicy가 갖고 있는 것들) : " + resultData.get(2));
                // 마지막 <youthPolicy> == get(101) 이자 102번 PolicyDTO
                log.info("DTO 내 마지막 youthPolicy 출력 : " + resultData.get(101));

                log.info("확인할 부분 여기야 여기");
                // 아무거나 꺼내서 로그로 확인해보기
                Map<String, Object> result = (Map<String, Object>) resultData.get(3);
                log.info(result.toString());

            } catch (Exception e) {
                log.info("에러 발생 >>> " + e.toString());
                e.printStackTrace();
            }

            log.info("resultData의 데이터 타입은 : " + resultData.getClass().getName() + "입니다.");


            int c = 0; // 이 친구가 DB에 값 들어갈 때마다 카운트를 세어줄 친구

            try {
                // 3번째부터가 youthPolicy 태그니까 get(3)부터 시작해야함
                // Jackson이 꺼내줄 때 맵으로 주니까 나도 맵으로 받아서 처리해야 함
                for (int p = 3; p < resultData.size(); p++) {
                    log.info(">>>>>>>>>>>>>>>>>>> 정책 상세정보 꺼내는 작업 시작");

                    Map<String, Object> policyInfo = (Map<String, Object>) resultData.get(p);

                    String rnum = CmmUtil.nvl((String) policyInfo.get("rnum"));                      // row번호
                    String ageInfo = CmmUtil.nvl((String) policyInfo.get("ageInfo"));                // 참여요건 - 연령
                    String bizId = CmmUtil.nvl((String) policyInfo.get("bizId"));                    // 정책 ID
                    String polyBizSecd = CmmUtil.nvl((String) policyInfo.get("polyBizSecd"));        // 정책일련번호
                    String polyBizTy = CmmUtil.nvl((String) policyInfo.get("polyBizTy"));            // 기관 및 지자체 구분
                    String polyBizSjnm = CmmUtil.nvl((String) policyInfo.get("polyBizSjnm"));        // 정책명
                    String polyItcnCn = CmmUtil.nvl((String) policyInfo.get("polyItcnCn"));          // 정책소개
                    String sporCn = CmmUtil.nvl((String) policyInfo.get("sporCn"));                  // 지원내용
                    String sporScvl = CmmUtil.nvl((String) policyInfo.get("sporScvl"));              // 지원규모
                    String rqutPrdCn = CmmUtil.nvl((String) policyInfo.get("rqutPrdCn"));            // 신청기간
                    String majrRqisCn = CmmUtil.nvl((String) policyInfo.get("majrRqisCn"));          // 참여요건 - 전공
                    String empmSttsCn = CmmUtil.nvl((String) policyInfo.get("empmSttsCn"));          // 참여요건 - 취업상태
                    String splzRlmRqisCn = CmmUtil.nvl((String) policyInfo.get("splzRlmRqisCn"));    // 참여요건 - 특화분야
                    String accrRqisCn = CmmUtil.nvl((String) policyInfo.get("accrRqisCn"));          // 참여요건 - 학력
                    String prcpCn = CmmUtil.nvl((String) policyInfo.get("prcpCn"));                  // 거주지 및 소득조건
                    String prcpLmttTrgtCn = CmmUtil.nvl((String) policyInfo.get("prcpLmttTrgtCn"));  // 참여제한대상
                    String rqutProcCn = CmmUtil.nvl((String) policyInfo.get("rqutProcCn"));          // 신청절차
                    String pstnPaprCn = CmmUtil.nvl((String) policyInfo.get("pstnPaprCn"));          // 제출서류
                    String jdgnPresCn = CmmUtil.nvl((String) policyInfo.get("jdgnPresCn"));          // 심사발표
                    String rqutUrla = CmmUtil.nvl((String) policyInfo.get("rqutUrla"));              // 신청 사이트 링크 주소
                    String cnsgNmor = CmmUtil.nvl((String) policyInfo.get("cnsgNmor"));              // 신청기관명
                    String polyRlmCd = CmmUtil.nvl((String) policyInfo.get("polyRlmCd"));             //정책유형코드

                    log.info("---------------api 호출 값(정책 상세 정보) 확인----------------");
                    log.info("1. row번호 : " + rnum);
                    log.info("2. 참여요건 - 연령 : " + ageInfo);
                    log.info("3. 정책 ID : " + bizId);
                    log.info("4. 정책일련번호 : " + polyBizSecd);
                    log.info("5. 기관 및 지자체 구분 : " + polyBizTy);
                    log.info("6. 정책명 : " + polyBizSjnm);
                    log.info("7. 정책소개 : " + polyItcnCn);
                    log.info("8. 지원내용 : " + sporCn);
                    log.info("9. 지원규모 : " + sporScvl);
                    log.info("10. 신청기간 : " + rqutPrdCn);
                    log.info("11. 참여요건(전공) : " + majrRqisCn);
                    log.info("12. 참여요건(취업상태) : " + empmSttsCn);
                    log.info("13. 참여요건(특화분야) : " + splzRlmRqisCn);
                    log.info("14. 참여요건(학력) : " + accrRqisCn);
                    log.info("15. 거주지 및 소득조건 : " + prcpCn);
                    log.info("16. 참여제한대상 : " + prcpLmttTrgtCn);
                    log.info("17. 신청절차 : " + rqutProcCn);
                    log.info("18. 제출서류 : " + pstnPaprCn);
                    log.info("19. 심사발표 : " + jdgnPresCn);
                    log.info("20. 신청 사이트 링크 주소 : " + rqutUrla);
                    log.info("21. 신청기관명 : " + cnsgNmor);
                    log.info("22. 정책 유형 코드 : " + polyRlmCd);
                    log.info("---------------api 호출 값(정책 상세 정보) 확인 종료----------------");

                    // DB에 저장하기 위해서 매퍼에 전달할 DTO 생성
                    ApiResultDTO pDTO = new ApiResultDTO();

                    pDTO.setRnum(rnum);
                    pDTO.setAge_info(ageInfo);
                    pDTO.setBiz_id(bizId);
                    pDTO.setPoly_biz_secd(polyBizSecd);
                    pDTO.setPoly_biz_ty(polyBizTy);
                    pDTO.setPoly_biz_sjnm(polyBizSjnm);
                    pDTO.setPoly_itcn_cn(polyItcnCn);
                    pDTO.setSpor_cn(sporCn);
                    pDTO.setSpor_scvl(sporScvl);
                    pDTO.setRqut_prd_cn(rqutPrdCn);
                    pDTO.setMajr_rqis_cn(majrRqisCn);
                    pDTO.setEmpm_stts_cn(empmSttsCn);
                    pDTO.setSplz_rlm_rqis_cn(splzRlmRqisCn);
                    pDTO.setAccr_rqis_cn(accrRqisCn);
                    pDTO.setPrcp_cn(prcpCn);
                    pDTO.setPrcp_lmtt_trgt_cn(prcpLmttTrgtCn);
                    pDTO.setRqut_proc_cn(rqutProcCn);
                    pDTO.setPstn_papr_cn(pstnPaprCn);
                    pDTO.setJdgn_pres_cn(jdgnPresCn);
                    pDTO.setRqut_urla(rqutUrla);
                    pDTO.setCnsg_nmor(cnsgNmor);
                    pDTO.setPoly_rlm_cd(polyRlmCd);
                    pDTO.setSrch_poly_biz_secd(srchPolyBizSecd);

                    int r = policyMapper.insertPolicyInfo(pDTO);
                    log.info("매퍼 함수 실행 결과(성공이면 1, 실패면 0) : " + r + "입니다.");

                    // DB에 저장됐으면 카운트
                    if (r == 1) {
                        c++;
                    }
                    log.info(">>>>>>>>>>>>>>>>>>> 정책 상세정보 데이터베이스에 저장 작업 완료");

                    // DTO 비우기
                    pDTO = null;
                }
                // 중간에 에러 생기면 처리
            } catch (Exception e) {
                log.info("에러 발생 >>> " + e.toString());
                e.printStackTrace();
            }

            // youthPolicy 개수만큼 DB에 저장하고 몇 개 저장되었는지 로그로 확인
            log.info("DB에 저장된 정책 개수는 : " + c + "개 입니다.");

            i++;
            pageIndex++;

            if (resultData.size() < 100) {
                switch (srchPolyBizSecd) {
                    case "003002002": {
                        srchPolyBizSecd = "003002003";
                        pageIndex = 1;
                        break;
                    }
                    case "003002003": {
                        srchPolyBizSecd = "003002004";
                        pageIndex = 1;
                        break;
                    }
                    case "003002004": {
                        srchPolyBizSecd = "003002005";
                        pageIndex = 1;
                        break;
                    }
                    case "003002005": {
                        srchPolyBizSecd = "003002006";
                        pageIndex = 1;
                        break;
                    }
                    case "003002006": {
                        srchPolyBizSecd = "003002007";
                        pageIndex = 1;
                        break;
                    }
                    case "003002007": {
                        srchPolyBizSecd = "003002008";
                        pageIndex = 1;
                        break;
                    }
                    case "003002008": {
                        srchPolyBizSecd = "003002009";
                        pageIndex = 1;
                        break;
                    }
                    case "003002009": {
                        srchPolyBizSecd = "003002010";
                        pageIndex = 1;
                        break;
                    }
                    case "003002010": {
                        srchPolyBizSecd = "003002011";
                        pageIndex = 1;
                        break;
                    }
                    case "003002011": {
                        srchPolyBizSecd = "003002012";
                        pageIndex = 1;
                        break;
                    }
                    case "003002012": {
                        srchPolyBizSecd = "003002013";
                        pageIndex = 1;
                        break;
                    }
                    case "003002013": {
                        srchPolyBizSecd = "003002014";
                        pageIndex = 1;
                        break;
                    }
                    case "003002014": {
                        srchPolyBizSecd = "003002015";
                        pageIndex = 1;
                        break;
                    }
                    case "003002015": {
                        srchPolyBizSecd = "003002016";
                        pageIndex = 1;
                        break;
                    }
                    case "003002016": {
                        srchPolyBizSecd = "003002017";
                        pageIndex = 1;
                        break;
                    }
                    default: {
                        break callAPI;
                    }
                }
            }

            areaParameter.add(srchPolyBizSecd);

        }

        log.info("파라미터로 들어간 지역 코드들은 : " + areaParameter.toString() + "입니다.");

        d = policyMapper.checkPolicyData();

        log.info("데이터베이스에 저장되어 있는 정책의 개수는 : " + d + "개 입니다.");

        log.info("최종 i 값은 : " + i + "입니다.");
        log.info(this.getClass().getName() + "블루밍 서비스 정책 API 호출 종료합니다.");

    }

    @Override
    public List<ApiResultDTO> getMainPolicyList() throws Exception {
        log.info(this.getClass().getName() + "메인 페이지에 띄울 최신 정책 10개 호출합니다.");

        return policyMapper.getMainPolicyList();
    }


    // <<정책 게시판 관련 함수들>>

    @Override
    public List<ApiResultDTO> getCountTable() throws Exception {
        log.info(this.getClass().getName() + "정책 게시판 진입, 정책 리스트 가져오기 서비스(getCountTable)실행");
        log.info(this.getClass().getName() + "정책 게시판 진입, 정책 리스트 가져오기 서비스(getCountTable)종료");

        return policyMapper.getCountTable();
    }

    @Override
    public List<ApiResultDTO> getCountAreaList(ApiResultDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "지역별 정책 리스트 가져오기 서비스(getCountAreaList)실행");

        List<ApiResultDTO> rList = policyMapper.getCountAreaList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "지역별 정책 리스트 가져오기 서비스(getCountAreaList)종료");

        return rList;
    }

    @Override
    public List<ApiResultDTO> getCountCategoryList(ApiResultDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "분야별 정책 리스트 가져오기 서비스(getCountCategoryList)실행");

        List<ApiResultDTO> rList = policyMapper.getCountCategoryList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "분야별 정책 리스트 가져오기 서비스(getCountCategoryList)종료");

        return rList;
    }

    @Override
    public List<ApiResultDTO> getCountOptionList(ApiResultDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "통합검색 정책 리스트 가져오기 서비스(getCountOptionList)실행");

        List<ApiResultDTO> rList = policyMapper.getCountOptionList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "통합검색 정책 리스트 가져오기 서비스(getCountOptionList)종료");

        return rList;
    }

    @Override
    public ApiResultDTO getPolicyInfo(ApiResultDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "정책 상세보기 서비스(getPolicyInfo) 실행");

        ApiResultDTO rDTO = policyMapper.getPolicyInfo(pDTO);

        log.info(this.getClass().getName() + "정책 상세보기 서비스(getPolicyInfo) 종료");

        return rDTO;
    }


}









