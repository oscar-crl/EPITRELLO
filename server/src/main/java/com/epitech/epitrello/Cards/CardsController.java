package com.epitech.epitrello.Cards;

import com.epitech.epitrello.Boards.Boards;
import com.epitech.epitrello.Boards.BoardsRepository;
import com.epitech.epitrello.Lists.ListsRepository;
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
public class CardsController {

    @Autowired
    private CardsRepository cardsRepository;

    @Autowired
    private ListsRepository listsRepository;

    @Autowired
    private BoardsRepository boardsRepository;

    @PostMapping(path="/cards")
    public ResponseEntity<?> addCard(@RequestParam String name, @RequestParam Integer idList, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!listsRepository.existsByIdBoardAndIdList(idBoard, idList))
            return new ResponseEntity<>("List doesnt exists", HttpStatus.BAD_REQUEST);
        Cards newCard = new Cards();
        newCard.setName(name);
        newCard.setIdList(idList);
        newCard.setIdBoard(idBoard);
        newCard.setPos(cardsRepository.countAllByIdBoardAndIdList(idBoard, idList) + 1);
        cardsRepository.save(newCard);
        Boards boards = boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString());
        boards.getLastActivity().add(user.getAttribute("User").toString() + " added " + name + " to " + listsRepository.findByIdBoardAndIdList(idBoard, idList).getName());
        boardsRepository.save(boards);
        return new ResponseEntity<>(newCard, HttpStatus.OK);
    }

    @GetMapping(path="/cards")
    public ResponseEntity<?> getCardById(@RequestParam Integer idCard, @RequestParam Integer idBoard, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!cardsRepository.existsByIdBoardAndIdCard(idBoard, idCard))
            return new ResponseEntity<>("Card doesnt exists", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(cardsRepository.findByIdBoardAndIdCard(idBoard, idCard), HttpStatus.OK);
    }

    private List<Cards> cleanPos(Integer idBoard, Integer idList) {
        List<Cards> cardsList = cardsRepository.findAllByIdBoardAndIdList(idBoard, idList);
        cardsList.sort(Comparator.comparing(Cards::getPos));
        int i = 1;
        for (Cards cards : cardsList) {
            if (cards.getPos() != i)
                cards.setPos(i);
            i++;
        }
        return cardsList;
    }

    @PutMapping(path="/cards")
    public ResponseEntity<?> updateCardById(@RequestParam Optional<String> name, @RequestParam Integer idCard,
                                            @RequestParam Optional<Integer> idList, @RequestParam Optional<Integer> pos,
                                            @RequestParam Integer idBoard, @RequestParam Optional<String> description,
                                            @RequestParam Optional<String> member, @RequestParam Optional<String> label,
                                            @RequestParam Optional<String> task, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (idList.isPresent() && !listsRepository.existsByIdBoardAndIdList(idBoard, idList.get()))
            return new ResponseEntity<>("List doesnt exists", HttpStatus.BAD_REQUEST);
        if (!cardsRepository.existsByIdBoardAndIdCard(idBoard, idCard))
            return new ResponseEntity<>("Card doesnt exists", HttpStatus.BAD_REQUEST);
        Cards tmpCard = cardsRepository.findByIdBoardAndIdCard(idBoard, idCard);
        Boards boards = boardsRepository.findByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString());
        name.ifPresent(tmpCard::setName);
        description.ifPresent(tmpCard::setDescription);
        if (label.isPresent())
            if (label.get().equals("done") || label.get().equals("on doing") || label.get().equals("to do")) {
                tmpCard.setLabel(label.get());
                if (label.get().equals("done"))
                    boards.getLastActivity().add(user.getAttribute("User").toString() + " completed " + tmpCard.getName());
                else if (label.get().equals("on doing"))
                    boards.getLastActivity().add(user.getAttribute("User").toString() + " is working on " + tmpCard.getName());
                else
                    boards.getLastActivity().add(user.getAttribute("User").toString() + " mark " + tmpCard.getName() + " as to do");
                boardsRepository.save(boards);
            }
        task.ifPresent(s -> tmpCard.getTasks().add(s));
        if (member.isPresent()) {
            if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, member.get()))
                tmpCard.getMember().add(member.get());
        }
        if (idList.isPresent() && pos.isPresent()) {
            tmpCard.setPos(pos.get() - 0.5);
            cardsRepository.save(tmpCard);
            Integer prevIdList = tmpCard.getIdList();
            tmpCard.setIdList(idList.get());
            cardsRepository.save(tmpCard);
            cardsRepository.saveAll(cleanPos(idBoard, idList.get()));
            cardsRepository.saveAll(cleanPos(idBoard, prevIdList));
            boards.getLastActivity().add(user.getAttribute("User").toString() + " moved " + tmpCard.getName() + " from " +
                    listsRepository.findByIdBoardAndIdList(idBoard, prevIdList).getName() + " to " + listsRepository.findByIdBoardAndIdList(idBoard, idList.get()).getName());
            boardsRepository.save(boards);
        } else if (pos.isPresent()) {
            Cards switchCard = cardsRepository.findByIdBoardAndIdListAndPos(idBoard, tmpCard.getIdList(), pos.get());
            switchCard.setPos(tmpCard.getPos());
            tmpCard.setPos(pos.get());
            cardsRepository.save(switchCard);
        }
        cardsRepository.save(tmpCard);
        return new ResponseEntity<>("Update card with id : " + idCard, HttpStatus.OK);
    }

    @DeleteMapping(path="/cards")
    public ResponseEntity<?> deleteCardById(@RequestParam Integer idCard, @RequestParam Integer idBoard, @RequestParam Optional<String> task, HttpServletRequest user) {
        if (!boardsRepository.existsByIdBoardAndIdUser(idBoard, user.getAttribute("User").toString()))
            return new ResponseEntity<>("User is not a member on this board", HttpStatus.BAD_REQUEST);
        if (!cardsRepository.existsByIdBoardAndIdCard(idBoard, idCard))
            return new ResponseEntity<>("Card doesnt exists", HttpStatus.BAD_REQUEST);
        Cards tmpCard = cardsRepository.findByIdBoardAndIdCard(idBoard, idCard);
        if (task.isPresent()) {
            tmpCard.getTasks().remove(task.get());
        } else {
            cardsRepository.delete(tmpCard);
            cardsRepository.saveAll(cleanPos(idBoard, tmpCard.getIdList()));
        }
        return new ResponseEntity<>("Delete card with id : " + idCard, HttpStatus.OK);
    }

    @GetMapping(path="/cards/all")
    public @ResponseBody Iterable<Cards> getCards() {
        return cardsRepository.findAll();
    }
}