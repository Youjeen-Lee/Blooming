package kopo.poly.controller;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.JobDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.WeatherDTO;
import kopo.poly.service.IJobService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping(value = "/job")  //컨트롤러마다 url 겹치면 에러남
@RequiredArgsConstructor
@Controller
public class JobController {

    private final IJobService JobService;


    //일자리 정보 리스트 화면 진입 시 실행
    @GetMapping(value = "getJobList")
    public String getJobList(ModelMap modelMap,
                             @RequestParam(defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + ".getJobList Start!");

        try {
            // 전체 데이터 수 가져오기
            List<JobDTO> rList = JobService.getJobList();

            if (rList == null) rList = new ArrayList<>();

            int itemsPerPage = 18;
            int totalItems = rList.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            int fromIndex = (page - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            rList = rList.subList(fromIndex, toIndex);

            // 등록일 가져오기
            String reg_dt = rList.get(5).getReg_dt();
            log.info("업데이트일 : " + reg_dt);

            // 가져온 데이터와 페이징 정보를 ModelMap에 추가
            modelMap.addAttribute("reg_dt", reg_dt);
            modelMap.addAttribute("rList", rList);
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("totalPages", totalPages);

            log.info("가져온 일자리의 개수는 : " + rList.size());

        } catch(Exception e) {
            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".getJobList End!");

        return "job/jobList";
    }

    //일자리 정보 상세보기 화면 진입 시 실행
    @GetMapping(value = "getJobInfo")
    public String getJobInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getJobInfo Start!");

        try {
            String job_seq = request.getParameter("job_seq");
            log.info("job_seq : " + job_seq);

            JobDTO pDTO = new JobDTO();
            pDTO.setJob_seq(Integer.parseInt(job_seq));

            JobDTO rDTO = JobService.getJobInfo(pDTO);
            log.info("일자리 상세 정보 조회 결과 : " + rDTO);
            // 화면으로 보냄
            model.addAttribute("rDTO", rDTO);

        } catch(Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".getJobInfo End!");

        return "job/jobInfo";
    }

    // 옵션 선택 후 일자리 가져오는 페이지
    @PostMapping(value = "/getJobOptionList")
    public String getPolicyOptionList(ModelMap modelMap,
                                      HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + "옵션별 일자리 호출 컨트롤러(getJobOptionList) 실행합니다.");

        String errorMsg = "";
        List<JobDTO> rList = new ArrayList<>();

        try {

            JobDTO pDTO = new JobDTO();

            String jobCode = CmmUtil.nvl(request.getParameter("jobCode"));
            String areaCode = CmmUtil.nvl(request.getParameter("areaCode"));

            log.info("들어온 근무 코드 : " + jobCode);
            log.info("들어온 지역 코드 : " + areaCode);

            // 지역 코드만 들어옴
            if ("".equals(jobCode) && !"".equals(areaCode)) {

                pDTO.setAreaCode(areaCode);
                rList = JobService.getCountAreaList(pDTO);

                // 업종 코드만 들어옴
            } else if (!"".equals(jobCode) && "".equals(areaCode)) {

                pDTO.setPosition_jobMidCode_Code(jobCode);
                rList = JobService.getCountCategoryList(pDTO);

                // 둘 다 들어옴
            } else if (!"".equals(jobCode) && !"".equals(areaCode)) {

                pDTO.setPosition_jobMidCode_Code(jobCode);
                pDTO.setAreaCode(areaCode);
                rList = JobService.getCountOptionList(pDTO);

            }

            log.info("DTO에 저장된 업종 코드 : " + pDTO.getPosition_jobMidCode_Code());
            log.info("DTO에 저장된 지역 코드 : " + pDTO.getAreaCode());


            log.info(rList.toString());

            if (rList == null) rList = new ArrayList<>();

            try {
                // 등록일 가져오기
                String reg_dt = rList.get(0).getReg_dt();
            } catch(Exception e) {
                log.info("에러 발생 >>>>> " + e.toString());
                e.printStackTrace();
            }

            int itemsPerPage = 18;
            int totalItems = rList.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            int fromIndex = (page - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            rList = rList.subList(fromIndex, toIndex);

            // 가져온 데이터와 페이징 정보를 ModelMap에 추가
            modelMap.addAttribute("rList", rList);
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("totalPages", totalPages);

            log.info("가져온 정책의 개수는 : " + rList.size());
        } catch (Exception e) {
            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + "옵션별 일자리 호출 컨트롤러(getJobOptionList) 종료합니다.");
        }

        return "job/jobList";
    }


    @ResponseBody //테스트용
    @GetMapping(value = "insertJobList")
    public int insertJobList() throws Exception {
        log.info(this.getClass().getName() + "insertJobList : 일자리 api 호출 및 db 저장 로직을 url 호출로 실행하기 시작");
        int result = JobService.insertJobList();
        log.info("호출 및 저장 개수 : "+result);
        log.info(this.getClass().getName() + "insertJobList : 일자리 api 호출 및 db 저장 로직을 url 호출로 실행하기 끝");
        return result;
    }

}
