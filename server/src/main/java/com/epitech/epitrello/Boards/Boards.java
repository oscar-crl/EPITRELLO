package com.epitech.epitrello.Boards;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
public class Boards {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer idBoard;

    private String name;

    @ElementCollection
    private List<String> idUser = new ArrayList<>();

    @ElementCollection
    private List<String> lastActivity = new ArrayList<>();

    public Integer getIdBoard() {
        return idBoard;
    }

    public void setIdBoard(Integer idBoard) {
        this.idBoard = idBoard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIdUser() {
        return idUser;
    }

    public void setIdUser(List<String> idUser) {
        this.idUser = idUser;
    }

    public List<String> getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(List<String> lastActivity) {
        this.lastActivity = lastActivity;
    }
}