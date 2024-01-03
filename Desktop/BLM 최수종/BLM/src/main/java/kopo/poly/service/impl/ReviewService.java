package kopo.poly.service.impl;

import kopo.poly.dto.ReviewDTO;
import kopo.poly.persistance.mapper.IReviewMapper;
import kopo.poly.service.IReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // noticeMapper 변수에 이미 메모리에 올라간 INoticeMapper 객체를 넣어줌
    // 예전에는 autowired 어노테이션를 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final IReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> getReviewList(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getReviewList start!");

        return reviewMapper.getReviewList(pDTO);

    }

    @Transactional
    @Override
    public void insertReviewInfo(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertReviewInfo start!");

        reviewMapper.insertReviewInfo(pDTO);
    }

    @Transactional
    @Override
    public void updateReviewInfo(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateReviewInfo start!");

        reviewMapper.updateReviewInfo(pDTO);

    }

    @Transactional
    @Override
    public void deleteReviewInfo(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteReviewInfo start!");

        reviewMapper.deleteReviewInfo(pDTO);

    }

}
