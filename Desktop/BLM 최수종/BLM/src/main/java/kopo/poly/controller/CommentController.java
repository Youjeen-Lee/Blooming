package kopo.poly.controller;

import kopo.poly.dto.CommentDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 * */
@Slf4j
//@RequestMapping(value = "/notice")
@RequiredArgsConstructor
@RestController //ResponseBody 전체적용
public class CommentController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final ICommentService commentService;


    /**
     * 게시판 글 등록
     * <p>
     * 게시글 등록은 Ajax로 호출되기 때문에 결과는 JSON 구조로 전달해야만 함
     * JSON 구조로 결과 메시지를 전송하기 위해 @ResponseBody 어노테이션 추가함
     */
    @GetMapping(value = "/comment/getCommentList")
    public List<CommentDTO> getCommentList(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".getCommentList Start!");
        int res = 0;

        List<CommentDTO> rList = new ArrayList<>();
        try {
            //조회할 게시글 번호를 받아옴
            String notice_seq = CmmUtil.nvl(request.getParameter("notice_seq"));
            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("notice_seq : " + notice_seq);

            // 데이터 전달하기 위해 DTO에 저장하기
            CommentDTO pDTO = new CommentDTO();
            pDTO.setNotice_seq(notice_seq);

            /*
             * 댓글 등록하기위한 비즈니스 로직을 호출
             */
            rList = commentService.getCommentList(pDTO);
            if (rList == null) {
                rList = new ArrayList<>();
            }
            log.info("조회된 댓글 수 : " + rList.size());


        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".getCommentList End!");
            return rList;
        }

    }

    /**
     * 댓글 등록
     * <p>
     * 게시글 등록은 Ajax로 호출되기 때문에 결과는 JSON 구조로 전달해야만 함
     * JSON 구조로 결과 메시지를 전송하기 위해 @ResponseBody 어노테이션 추가함
     */
    @PostMapping(value = "/comment/commentInsert")
    public int noticeInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".commentInsert Start!");
        int res = 0;
        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 제목
            String notice_seq = CmmUtil.nvl(request.getParameter("notice_seq")); // 내용
            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session user_id : " + user_id);
            log.info("contents : " + contents);
            log.info("notice_seq : " + notice_seq);

            // 데이터 저장하기 위해 DTO에 저장하기
            CommentDTO pDTO = new CommentDTO();
            pDTO.setUser_id(user_id);
            pDTO.setContents(contents);
            pDTO.setNotice_seq(notice_seq);

            /*
             * 댓글 등록하기위한 비즈니스 로직을 호출
             */
            commentService.insertCommentInfo(pDTO);

            res = 1;

        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".noticeInsert End!");
            return res;
        }

    }

    /**
     * 게시판 글 수정 실행 로직
     */
    @PostMapping(value = "/comment/commentUpdate")
    public int noticeUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        int res = 0;
        try {
            String comment_seq = CmmUtil.nvl(request.getParameter("comment_seq")); // 제목
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

            log.info("comment_seq : " + comment_seq);
            log.info("contents : " + contents);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CommentDTO pDTO = new CommentDTO();
            pDTO.setComment_seq(comment_seq);
            pDTO.setContents(contents);

            // 게시글 수정하기 DB
            commentService.updateCommentInfo(pDTO);

            res = 1;
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".noticeUpdate End!");
            return res;
        }
    }

    /**
     *  댓글 삭제
     */
    @GetMapping(value = "/comment/commentDelete")
    public int commentDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".commentDelete Start!");

        int res = 0;
        try {
            String comment_seq = CmmUtil.nvl(request.getParameter("comment_seq")); // 글번호(PK)
            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("comment_seq : " + comment_seq);
            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CommentDTO pDTO = new CommentDTO();
            pDTO.setComment_seq(comment_seq);

            // 게시글 삭제하기 DB
            commentService.deleteCommentInfo(pDTO);

            res = 1;

        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".commentDelete End!");
            return res;
        }
    }

}
