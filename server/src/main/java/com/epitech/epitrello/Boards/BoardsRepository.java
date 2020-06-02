package com.epitech.epitrello.Boards;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BoardsRepository extends CrudRepository<Boards, Integer> {
    boolean existsByIdBoard(Integer idBoard);
    Boards findByIdBoardAndIdUser(Integer idBoard, String idUser);
    boolean existsByIdBoardAndIdUser(Integer idBoard, String idUser);
    List<Boards> findByIdUser(String idUser);
}