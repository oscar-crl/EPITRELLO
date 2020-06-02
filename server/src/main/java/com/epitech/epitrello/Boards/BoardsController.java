package com.epitech.epitrello.Boards;

import com.epitech.epitrello.Cards.CardsRepository;
import com.epitech.epitrello.Lists.Lists;
import com.epitech.epitrello.Lists.ListsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping(path="/epitrello")
public class BoardsController {

    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private ListsRepository listsRepository;

    @Autowired
    private CardsRepository cardsRepository;

    @PostMapping(path="/boards")
    public ResponseEntity<?> addBoard(@RequestParam String name, HttpServletRequest user) {
        Boards boards = new Boards();
        boards.setName(name);
        boards.getIdUser().add(user.getAttribute("User").toString());
        boards.getLastActivity().add(user.getAttribute("User").toString() + " created this board");
        boardsRepository.save(boards);
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping(path="/boards")
    public ResponseEntity<?> getUserBoard(HttpServletRequest user) {
        return new ResponseEntity<>(boardsRepository.findByIdUser(user.getAttribute("User").toString()), HttpStatus.OK);
    }

    @GetMapping(path="/boards/load")
    public ResponseEntity<?> getUserBoardById(@RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        List<Object> boardContent = new ArrayList<>();
        for (Lists list : listsRepository.findAllByIdBoard(idBoard)) {
            Map<String, Object> tmpMap = new HashMap<>();
            tmpMap.put("idList", list.getIdList());
            tmpMap.put("name", list.getName());
            tmpMap.put("pos", list.getPos());
            tmpMap.put("cards", cardsRepository.findAllByIdBoardAndIdList(idBoard, list.getIdList()));
            boardContent.add(tmpMap);
        }
        return new ResponseEntity<>(boardContent, HttpStatus.OK);
    }

    @GetMapping(path="/boards/activity")
    public ResponseEntity<?> getActivityFromBoardById(@RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()).getLastActivity(), HttpStatus.OK);
    }

    @PutMapping(path="/boards")
    public ResponseEntity<?> updateUserBoard(@RequestParam Optional<String> name, @RequestParam Optional<String> idMember, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        Boards boards = boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString());
        if (name.isPresent()) {
            boards.setName(name.get());
            boards.getLastActivity().add(user.getAttribute("User").toString() + " change name of this board to " + name.get());
        }
        if (idMember.isPresent()) {
            boards.getIdUser().add(idMember.get());
            boards.getLastActivity().add(user.getAttribute("User").toString() + " added " + idMember.get() + " to this board");
        }
        return new ResponseEntity<>(boardsRepository.save(boards), HttpStatus.OK);
    }

    @DeleteMapping(path="/boards")
    public ResponseEntity<?> deleteUserBoard(@RequestParam Integer idBoard, @RequestParam Optional<String> idMember, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        Boards boards = boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString());
        if (idMember.isPresent()) {
            boards.getIdUser().remove(idMember.get());
            return new ResponseEntity<>("Removed user " + idMember.get() + "from board with id : " + idBoard.toString(), HttpStatus.OK);
        }
        boardsRepository.delete(boards);
        listsRepository.deleteAll(listsRepository.findAllByIdBoard(idBoard));
        cardsRepository.deleteAll(cardsRepository.findAllByIdBoard(idBoard));
        return new ResponseEntity<>("Delete Board with id : " + idBoard.toString(), HttpStatus.OK);
    }

    @GetMapping(path="/boards/all")
    public @ResponseBody Iterable<Boards> getBoards() {
        return boardsRepository.findAll();
    }
}