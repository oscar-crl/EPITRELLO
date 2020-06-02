package com.epitech.epitrello.Lists;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ListsRepository extends CrudRepository<Lists, Integer> {
    Integer countAllByIdBoard(Integer idBoard);
    List<Lists> findAllByIdBoard(Integer idBoard);
    Lists findByIdBoardAndIdList(Integer idBoard, Integer idList);
    Lists findByIdBoardAndPos(Integer idBoard, Integer pos);
    boolean existsByIdBoardAndIdList(Integer idBoard, Integer idList);
}