package com.epitech.epitrello.Cards;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
public class Cards {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer idCard;

    private String name;

    private String description;

    @ElementCollection
    private List<String> member = new ArrayList<>();

    @JsonIgnore
    private Integer idList;

    @JsonIgnore
    private Integer idBoard;

    private String label;

    @ElementCollection
    private List<String> tasks = new ArrayList<>();

    private double pos;

    public Integer getIdCard() {
        return idCard;
    }

    public void setIdCard(Integer idCard) {
        this.idCard = idCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }

    public Integer getIdList() {
        return idList;
    }

    public void setIdList(Integer idList) {
        this.idList = idList;
    }

    public Integer getIdBoard() {
        return idBoard;
    }

    public void setIdBoard(Integer idBoard) {
        this.idBoard = idBoard;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}