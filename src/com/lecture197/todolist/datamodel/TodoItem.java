package com.lecture197.todolist.datamodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

public class TodoItem {

    @Setter
    @Getter
    private String shortDesc;
    @Setter
    @Getter
    private String details;
    @Setter
    @Getter
    private LocalDate deadLine;

    public TodoItem(String shortDesc, String details, LocalDate deadLine) {
        this.shortDesc = shortDesc;
        this.details = details;
        this.deadLine = deadLine;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "shortDesc='" + shortDesc + '\'' +
                ", details='" + details + '\'' +
                ", deadLine=" + deadLine +
                '}';
    }
}
