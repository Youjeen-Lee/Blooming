package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.JobDTO;

import kopo.poly.dto.WeatherDTO;
import kopo.poly.dto.WeatherDailyDTO;
import kopo.poly.persistance.mapper.IJobMapper;
import kopo.poly.service.IJobService;
import kopo.poly.service.IWeatherService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobService implements IJobService {

    @Value("${job.api.key}")
    private String apiKey;

    private final IJobMapper jobMapper;

    /**
     * 일자리 api 호출해 db에 저장
     */
    @Scheduled(cron = "0 50 9 * * MON-FRI")
//    @Scheduled(fixedDelay = 999999999, initialDelay = 500)
    @Override
    public int insertJobList() throws Exception {

        log.info(this.getClass().getName() + ".insertJobList 시작!!");

        // header 값 보낼 변수 선언 후 networkUtil로 보내기
        // --> 소영 say) 한 번 저장된 값을 계속 쓰니까 굳이 반복문 안에 넣지 않아도 된다!
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Accept", "application/json");

        // 반복문 밖에서 선언해야 반복으로 얻은 결과를 누적할 수 있어
        // 반복문 안에서 선언되면 한번의 반복이 끝날 때마다 초기화되기 때문~
        List<JobDTO> rList = new ArrayList<>();

        int start = 0;

        //11000
        for (int i = 0; i < 90; i++) {
            // --> 소영 say) 반복문의 횟수만 커지면 의미가 없음 같은 데이터를 여러 번 가져오는 게 되기 때문에 반복문을 돌리면서
            // "1영역 2영역 3영역 ... 끝까지" 이렇게 전체 데이터를 처음부터 끝까지 다 훑는 게 포인트임
            // 그러려면 영역을 담당하는 친구가 계속 다른 값을 전달해줘야 이동을 하면서 전체를 훑을 수가 있는 것!!!

            /**
             * https://oapi.saramin.co.kr/job-search?
             * access-key=XxqB875YTmYoVYRYENiROz1IX4BgU3lXnQU2Cvg9BWAe2UwuHa
             * &count=110
             */

            // 요청 파라미터(순서는 상관없음) ? ==> 요청 매개변수의 시작점, & ==> 다수의 요청변수를 연결, ? 뒤에 키-밸류 형식이기만 하면 순서 상관없음
            String apiParam = "?access-key=" + apiKey + "&count=110" + "&start=" + start; // 요청변수에서 호출할 때마다 110개씩 가져오게함
            // --> 소영 say) start(요청 페이지)라는 변수가 계속 바뀌어야 하고 그에 따라 뒤에 붙일 최종 파라미터의 값이 바뀌기 때문에 이건 반복문 안에 설정해야 한다!
            // 그럼 이 start 변수의 값은 어디서 바꿔? 반복문의 가장 아래 부분에서 바꾸면 됨!
            // 첫 번째 반복이 끝나고 아래 부분에서 미리 값을 바꿔놔야 두 번째 반복이 실행될 때 바뀐 값이 파라미터에 들어가니까!
            // 주의) 그럼 start는 왜 반복문 바깥에 선언해둔 거야? 저 start는 이 변수를 쓸 거고, 맨 처음엔 0으로 시작할 거라고 알려주는 애
            // 저게 반복문 안으로 들어오면 내가 아래에서 start에 들어가는 값을 1, 2, 3, 4 로 바꿔봤자 이 부분에서 0으로 다시 초기화 되어 버림

            log.info("apiParam " + apiParam);

            // URL + 파라미터
            String json = NetworkUtil.get(IJobService.apiURL + apiParam, requestHeaders);
            log.info("json : " + json);

            // JSON 구조를 Map 데이터 구조로 변경하기
            // 키와 값 구조의 JSON구조로부터 데이터를 쉽게 가져오기 위해 Map 데이터구조로 변경함
            Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

            // 일자리 정보를 가지고 있는 Jobs 키의 값 가져오기
            Map<String, Object> jobs = (Map<String, Object>) rMap.get("jobs");

            // 매개변수 따로 없으면 count=110로 요청했으므로 110개 제공
            List<Map<String, Object>> jobList = (List<Map<String, Object>>) jobs.get("job"); // Jobs > Job
            log.info("조회된 채용 정보 리스트 개수 : " + jobList.size());


            for (Map<String, Object> jobElement : jobList) {

                // company {}
                Map<String, Object> company = (Map<String, Object>) jobElement.get("company");
                Map<String, Object> company_detail = (Map<String, Object>) company.get("detail");
                String company_detail_name = (String) company_detail.get("name");
                String company_detail_href = (String) company_detail.get("href");

                // position {}
                Map<String, Object> position = (Map<String, Object>) jobElement.get("position");
                String position_title = (String) position.get("title");

                Map<String, Object> position_jobMidCode = (Map<String, Object>) position.get("job-mid-code");
                String position_jobMidCode_name = (String) position_jobMidCode.get("name");
                String position_jobMidCode_code = (String) position_jobMidCode.get("code");

                Map<String, Object> position_location = (Map<String, Object>) position.get("location");
                String position_location_name = (String) position_location.get("name");

                Map<String, Object> position_jobType = (Map<String, Object>) position.get("job-type");
                String position_jobType_name = (String) position_jobType.get("name");

                Map<String, Object> position_experienceLevel = (Map<String, Object>) position.get("experience-level");
                String position_experienceLevel_name = (String) position_experienceLevel.get("name");

                Map<String, Object> position_requiredEducationLevel = (Map<String, Object>) position.get("required-education-level");
                String position_requiredEducationLevel_name = (String) position_requiredEducationLevel.get("name");

                // salary {}
                Map<String, Object> salary = (Map<String, Object>) jobElement.get("salary");
                String salary_name = (String) salary.get("name");

                // expirationDate (String)
                String expirationDate = (String) jobElement.get("expiration-timestamp");

                JobDTO jobDTO = new JobDTO();

                jobDTO.setCompany_detail_name(company_detail_name);
                jobDTO.setCompany_detail_href(company_detail_href);

                jobDTO.setPosition_title(position_title);
                jobDTO.setPosition_jobMidCode_name(position_jobMidCode_name);
                jobDTO.setPosition_jobMidCode_Code(position_jobMidCode_code);
                jobDTO.setPosition_location_name(position_location_name);
                jobDTO.setPosition_jobType_name(position_jobType_name);
                jobDTO.setPosition_experienceLevel_name(position_experienceLevel_name);
                jobDTO.setPosition_requiredEducationLevel_name(position_requiredEducationLevel_name);

                jobDTO.setSalary_name(salary_name);
                jobDTO.setExpirationDate(expirationDate);

                rList.add(jobDTO);
            }

            start++;
        }

        log.info("반복문 끝난 후 리스트에 저장된 일자리 정보 개수 : " + rList.size() );
        // 기존 테이블 비우기
        jobMapper.truncateJobInfo();

        //api 조회 결과로 가져온 리스트 개수만큼 테이블에 추가
        int result = 0;
        for (JobDTO jobDTO : rList) {
            jobMapper.insertJobInfo(jobDTO);
            result++;
        }

        log.info(this.getClass().getName() + ".insertJobList 끝!!");

        return result;
    }

    @Override
    public List<JobDTO> getJobList() throws Exception {
        log.info(this.getClass().getName() + ".getJobList 시작!!");
        List<JobDTO> rList = jobMapper.getJobList();
        if (rList == null) rList = new ArrayList<>();
        log.info(this.getClass().getName() + ".getJobList 끝!!");
        return rList;
    }

    @Override
    public List<JobDTO> getMainJobList() throws Exception {
        log.info(this.getClass().getName() + "메인 페이지에 띄울 최신 일자리 10개 호출합니다.");

        return jobMapper.getMainJobList();
    }

    @Override
    public JobDTO getJobInfo(JobDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getJobInfo 시작!!");
        JobDTO rDTO = jobMapper.getJobInfo(pDTO);
        if (rDTO == null) rDTO = new JobDTO();
        log.info(this.getClass().getName() + ".getJobInfo 끝!!");
        return rDTO;
    }

    @Override
    public List<JobDTO> getCountAreaList(JobDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "지역별 일자리 리스트 가져오기 서비스(getCountAreaList)실행");

        List<JobDTO> rList = jobMapper.getCountAreaList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "지역별 일자리 리스트 가져오기 서비스(getCountAreaList)종료");

        return rList;
    }

    @Override
    public List<JobDTO> getCountCategoryList(JobDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "업종별 일자리 리스트 가져오기 서비스(getCountAreaList)실행");

        List<JobDTO> rList = jobMapper.getCountCategoryList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "업종별 일자리 리스트 가져오기 서비스(getCountAreaList)종료");

        return rList;
    }

    @Override
    public List<JobDTO> getCountOptionList(JobDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + "지역 + 업종별 일자리 리스트 가져오기 서비스(getCountAreaList)실행");

        List<JobDTO> rList = jobMapper.getCountOptionList(pDTO);
        log.info("조건에 해당하는 정책의 개수는 : " + rList.size() + "개 입니다.");

        log.info(this.getClass().getName() + "지역 + 업종별 일자리 리스트 가져오기 서비스(getCountAreaList)종료");

        return rList;
    }

}

