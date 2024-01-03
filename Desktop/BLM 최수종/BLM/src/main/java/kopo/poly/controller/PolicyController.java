package kopo.poly.controller;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.service.IPolicyService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/policy")
public class PolicyController {

    private final IPolicyService policyService2;

    // api 호출할 때 테스트 해보려고 넣어놨었음
    @GetMapping()
    public String getPolicyApi() throws Exception {
        log.info("test called");
        policyService2.getPolicyApi();

        return "/test";
    }

    // 정책 게시판 가져오는 페이지
    @GetMapping(value = "getPolicyMainPage")
    public String getPolicyMainPage(ModelMap modelMap,
                                    @RequestParam(defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + "정책 게시판 호출 컨트롤러(getPolicyMainPage) 실행합니다.");

        try {
            // 전체 데이터 수 가져오기
            List<ApiResultDTO> rList = policyService2.getCountTable();

            if (rList == null) rList = new ArrayList<>();

            int itemsPerPage = 18;
            int totalItems = rList.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            int fromIndex = (page - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            rList = rList.subList(fromIndex, toIndex);

            // 등록일 가져오기
            String reg_dt = rList.get(1).getReg_dt();

            // 가져온 데이터와 페이징 정보를 ModelMap에 추가
            modelMap.addAttribute("reg_dt", reg_dt);
            modelMap.addAttribute("rList", rList);
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("totalPages", totalPages);

            log.info("가져온 정책의 개수는 : " + rList.size());
        } catch (Exception e) {
            log.info("에러 발생 >>>>> " + e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + "정책 게시판 호출 컨트롤러(getPolicyMainPage) 종료합니다.");
        }

        return "/policy/policyList";
    }

    // 옵션 선택 후 정책 리스트 가져오는 페이지
    @PostMapping(value = "/getPolicyOptionList")
    public String getPolicyOptionList(ModelMap modelMap,
                                      HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") int page) throws Exception {
        log.info(this.getClass().getName() + "옵션별 정책 리스트 호출 컨트롤러(getPolicyOptionList) 실행합니다.");

        try {

            List<ApiResultDTO> rList = new ArrayList<>();
            ApiResultDTO pDTO = new ApiResultDTO();

            String polyCode = CmmUtil.nvl(request.getParameter("polyCode"));
            String areaCode = CmmUtil.nvl(request.getParameter("areaCode"));

            log.info("들어온 정책 코드 : " + polyCode);
            log.info("들어온 지역 코드 : " + areaCode);

            // 지역 코드만 들어옴
            if ("".equals(polyCode) && !"".equals(areaCode)) {

                pDTO.setAreaCode(areaCode);
                rList = policyService2.getCountAreaList(pDTO);

                // 정책 분야 코드만 들어옴
            } else if (!"".equals(polyCode) && "".equals(areaCode)) {

                pDTO.setPolyCode(polyCode);
                rList = policyService2.getCountCategoryList(pDTO);

                // 둘 다 들어옴
            } else if (!"".equals(polyCode) && !"".equals(areaCode)) {

                pDTO.setPolyCode(polyCode);
                pDTO.setAreaCode(areaCode);
                rList = policyService2.getCountOptionList(pDTO);

            }

            log.info("DTO에 저장된 정책 코드 : " + pDTO.getPolyCode());
            log.info("DTO에 저장된 지역 코드 : " + pDTO.getAreaCode());


            if (rList == null) rList = new ArrayList<>();

            try {
                // 등록일 가져오기
                String reg_dt = rList.get(0).getReg_dt();
                modelMap.addAttribute("reg_dt", reg_dt);
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
            log.info(this.getClass().getName() + "옵션별 정책 리스트 호출 컨트롤러(getPolicyOptionList) 종료합니다.");
        }

        return "/policy/policyList";
    }

    // 정책 상세보기
    @GetMapping(value = "/policyInfo")
    public String getPolicyInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + " 게시물 상세보기 컨트롤러(getPolicyInfo) 실행!");

        try {
            String seq = CmmUtil.nvl(request.getParameter("seq"));

            log.info("seq : " + seq);

            ApiResultDTO pDTO = new ApiResultDTO();

            pDTO.setSeq(seq);

            ApiResultDTO rDTO = Optional.ofNullable(policyService2.getPolicyInfo(pDTO)).orElseGet(ApiResultDTO::new);

            // 가져온 값 로그 싹 다 찍어보기  --> 이거 아니야 학교가서 다시 확인해
            log.info(rDTO.toString());

            modelMap.addAttribute("rDTO", rDTO);

        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + " 게시물 상세보기 컨트롤러(getPolicyInfo) 종료!");
        }

        return "/policy/policyInfo";
    }


}
