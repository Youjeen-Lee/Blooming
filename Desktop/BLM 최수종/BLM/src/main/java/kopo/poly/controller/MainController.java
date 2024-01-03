package kopo.poly.controller;

import kopo.poly.dto.ApiResultDTO;
import kopo.poly.dto.JobDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.StoreDTO;
import kopo.poly.service.IJobService;
import kopo.poly.service.INoticeService;
import kopo.poly.service.IPolicyService;
import kopo.poly.service.IStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    private final INoticeService noticeService;
    private final IJobService jobService;
    private final IPolicyService policyService;
    private final IStoreService storeService;

    // 메인화면에 들어갈 블럭 4개
    @GetMapping(value="/main")
    public String main(ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + "블루밍 서비스 메인화면 호출 시작합니다.");

        // 1 - 최신 정책 10개
        List<ApiResultDTO> pList = policyService.getMainPolicyList();

        // 2 - 최신 일자리 10개
        List<JobDTO> jList = jobService.getMainJobList();

        // 4 - 커뮤니티 최신글 10개
        List<NoticeDTO> cList = noticeService.getMainNoticeList();

        StoreDTO sDTO = storeService.getMainStoreInfo();

        modelMap.addAttribute("pList", pList); //policy라서 pList
        modelMap.addAttribute("jList", jList); //job이라서 jList
        modelMap.addAttribute("cList", cList); //community라서 cList
        modelMap.addAttribute("sDTO", sDTO);   //store라서 sDTO

        log.info(this.getClass().getName() + "블루밍 서비스 메인화면 호출 종료합니다.");

        return "/main";
    }
}
