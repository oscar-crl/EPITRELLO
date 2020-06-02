package com.epitech.epitrello.Cards;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardsRepository extends CrudRepository<Cards, Integer> {
    List<Cards> findAllByIdBoard(Integer idBoard);
    Integer countAllByIdBoardAndIdList(Integer idBoard, Integer idList);
    List<Cards> findAllByIdBoardAndIdList(Integer idBoard, Integer idList);
    Cards findByIdBoardAndIdCard(Integer idBoard, Integer idCard);
    Cards findByIdBoardAndIdListAndPos(Integer idBoard, Integer idList, double pos);
    boolean existsByIdBoardAndIdCard(Integer idBoard, Integer idCard);
}