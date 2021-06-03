package com.lecture197.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static final TodoData instance = new TodoData();
    private static final String filename = "TodoFile.todo";
    private final DateTimeFormatter dateFormatter;
    @Getter
    @Setter
    private ObservableList<TodoItem> todoItems;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        System.out.println("instantiate tododata");
        dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    }

    public void loadTodoItems() throws IOException{
        System.out.println("load tododata");
        todoItems = FXCollections.observableArrayList();
        Path filePath = Paths.get(filename);

        String input;
        TodoItem todoItem;

        try(BufferedReader br = Files.newBufferedReader(filePath)) {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");
                String shortDesc = itemPieces[0];
                String details = itemPieces[1];
                LocalDate dueDate = LocalDate.parse(itemPieces[2],dateFormatter);

                todoItem = new TodoItem(shortDesc, details, dueDate);
                todoItems.add(todoItem);
            }
        }
    }

    public void saveTodoItems() throws IOException {
        System.out.println("save tododata");
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        Iterator<TodoItem> iterator = todoItems.listIterator();

        try {
            while (iterator.hasNext()) {
                TodoItem item = iterator.next();
                String shortDesc = item.getShortDesc();
                String details = item.getDetails();
                String dueDate = item.getDeadLine().format(dateFormatter);
                bw.write(String.format("%s\t%s\t%s",shortDesc,details,dueDate));
                bw.newLine();
            }
        } finally {
            bw.close();
        }
    }

    public static boolean isDataFileExist() {
        Path filePath = Paths.get(filename);
        return filePath.toFile().exists();
    }

    public void addTodoItem(TodoItem todoItem) {
        this.todoItems.add(todoItem);
    }

    public void deleteTodoItem(TodoItem item) {
        this.todoItems.remove(item);
    }
}
