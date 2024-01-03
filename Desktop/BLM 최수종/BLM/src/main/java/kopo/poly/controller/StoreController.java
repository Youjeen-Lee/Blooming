package kopo.poly.controller;

import kopo.poly.dto.StoreDTO;
import kopo.poly.service.IStoreService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value="/store")
@RequiredArgsConstructor
public class StoreController {

    private final IStoreService storeService;

    @GetMapping(value="/storeReg")
    public String storeReg() throws Exception {
        log.info(this.getClass().getName() + ".청년점포 등록 페이지로 이동 컨트롤러 실행합니다.");

        return "/store/storeReg";
    }

    @PostMapping(value="/insertStoreInfo")
    public String insertStoreInfo(HttpServletRequest request, @RequestParam("files") List<MultipartFile> files, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".청춘활짝 블루밍, 청년점포 등록 컨트롤러 요청 받았습니다.");

        String msg = "";
        String url = "";

        try {
            String user_id = "flower001";
            String store_name = CmmUtil.nvl(request.getParameter("store_name"));
            String store_owner_name = CmmUtil.nvl(request.getParameter("store_owner_name"));
            String store_introduce = CmmUtil.nvl(request.getParameter("store_introduce"));
            String store_addr = CmmUtil.nvl(request.getParameter("store_addr"));
            String store_addr2 = CmmUtil.nvl(request.getParameter("store_addr2"));
            String store_call = CmmUtil.nvl(request.getParameter("store_call"));

            StoreDTO pDTO = new StoreDTO();
            pDTO.setUser_id(user_id);
            pDTO.setStore_name(store_name);
            pDTO.setStore_owner_name(store_owner_name);
            pDTO.setStore_introduce(store_introduce);
            pDTO.setStore_addr(store_addr);
            pDTO.setStore_addr2(store_addr2);
            pDTO.setStore_call(store_call);

            int i = storeService.insertStoreInfo(pDTO, files);

            log.info("청년점포 등록 결과(성공 1, 실패 0) : " + i);

            if(i == 1) {
                url = "/store/storeList";
                msg = "점포가 등록되었습니다.";
                modelMap.addAttribute("url", url);
                modelMap.addAttribute("msg", msg);
            }

        } catch(Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".청춘활짝 블루밍, 청년점포 등록 컨트롤러 요청 종료합니다.");

        return "/redirect";
    }

    @GetMapping(value="/storeList")
    public String getStoreList(ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".점포 게시판 진입, 청년점포 리스트 가져오기 컨트롤러 실행합니다.");

        List<StoreDTO> rList = storeService.getStoreList();

        modelMap.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".점포 게시판 진입, 청년점포 리스트 가져오기 컨트롤러 종료합니다.");

        return "/store/storeList";
    }

    @GetMapping(value="/getStoreInfo")
    public String getStoreInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".청년점포 상세보기 컨트롤러 실행합니다.");

       String store_seq = CmmUtil.nvl(request.getParameter("store_seq"));

        StoreDTO pDTO = new StoreDTO();
        pDTO.setStore_seq(store_seq);

        StoreDTO rDTO = storeService.getStoreInfo(pDTO);

        log.info("메인 이미지 경로 : " + rDTO.getStore_main_photo());

        modelMap.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".청년점포 상세보기 컨트롤러 종료합니다.");

        return "/store/storeInfo";

    }


}