package com.epitech.epitrello.Lists;

import com.epitech.epitrello.Boards.Boards;
import com.epitech.epitrello.Boards.BoardsRepository;
import com.epitech.epitrello.Cards.CardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(path="/epitrello")
public class ListsController {

    @Autowired
    private CardsRepository cardsRepository;

    @Autowired
    private ListsRepository listsRepository;

    @Autowired
    private BoardsRepository boardsRepository;

    @PostMapping(path="/lists")
    public ResponseEntity<?> addList(@RequestParam String name, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        Lists newList = new Lists();
        newList.setName(name);
        newList.setIdBoard(idBoard);
        newList.setPos(listsRepository.countAllByIdBoard(idBoard) + 1);
        listsRepository.save(newList);
        Boards boards = boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString());
        boards.getLastActivity().add(user.getAttribute("User").toString() + " added " + name + " to this board");
        boardsRepository.save(boards);
        return new ResponseEntity<>(newList, HttpStatus.OK);
    }

    @GetMapping(path="/lists")
    public ResponseEntity<?> getListById(@RequestParam Integer idList, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!listsRepository.existsByIdBoardAndIdList(idBoard, idList))
            return new ResponseEntity<>("List doesnt exists", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(listsRepository.findByIdBoardAndIdList(idBoard, idList), HttpStatus.OK);
    }

    private List<Lists> cleanPos(Integer idBoard) {
        List<Lists> listLists = listsRepository.findAllByIdBoard(idBoard);
        listLists.sort(Comparator.comparing(Lists::getPos));
        int i = 1;
        for (Lists lists : listLists) {
            if (lists.getPos() != i)
                lists.setPos(i);
            i++;
        }
        return listLists;
    }

    @PutMapping(path="/lists")
    public ResponseEntity<?> updateListById(@RequestParam Optional<String> name, @RequestParam Integer idList,
                                            @RequestParam Integer idBoard, @RequestParam Optional<Integer> pos,
                                            HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!listsRepository.existsByIdBoardAndIdList(idBoard, idList))
            return new ResponseEntity<>("List doesnt exists", HttpStatus.BAD_REQUEST);
        Lists tmpList = listsRepository.findByIdBoardAndIdList(idBoard, idList);
        if (pos.isPresent()) {
            Lists switchList = listsRepository.findByIdBoardAndPos(idBoard, pos.get());
            switchList.setPos(tmpList.getPos());
            tmpList.setPos(pos.get());
            listsRepository.save(switchList);
        }
        name.ifPresent(tmpList::setName);
        listsRepository.save(tmpList);
        return new ResponseEntity<>("Update list with id : " + idList, HttpStatus.OK);
    }

    @DeleteMapping(path="/lists")
    public ResponseEntity<?> deleteListById(@RequestParam Integer idList, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!listsRepository.existsByIdBoardAndIdList(idBoard, idList))
            return new ResponseEntity<>("List doesnt exists", HttpStatus.BAD_REQUEST);
        listsRepository.delete(listsRepository.findByIdBoardAndIdList(idBoard, idList));
        cardsRepository.deleteAll(cardsRepository.findAllByIdBoardAndIdList(idBoard, idList));
        listsRepository.saveAll(cleanPos(idBoard));
        return new ResponseEntity<>("Delete list with id : " + idList, HttpStatus.OK);
    }

    @GetMapping(path="/lists/all")
    public @ResponseBody Iterable<Lists> getLists() {
        return listsRepository.findAll();
    }
}