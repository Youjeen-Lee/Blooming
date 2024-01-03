package kopo.poly.service.impl;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.persistance.mapper.ICommentMapper;
import kopo.poly.persistance.mapper.INoticeMapper;
import kopo.poly.service.ICommentService;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // noticeMapper 변수에 이미 메모리에 올라간 INoticeMapper 객체를 넣어줌
    // 예전에는 autowired 어노테이션를 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final ICommentMapper commentMapper;

    @Override
    public List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getCommentList start!");

        return commentMapper.getCommentList(pDTO);

    }

    @Transactional
    @Override
    public void insertCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCommentInfo start!");

        commentMapper.insertCommentInfo(pDTO);
    }

    @Transactional
    @Override
    public void updateCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateCommentInfo start!");

        commentMapper.updateCommentInfo(pDTO);

    }

    @Transactional
    @Override
    public void deleteCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCommentInfo start!");

        commentMapper.deleteCommentInfo(pDTO);

    }

}
