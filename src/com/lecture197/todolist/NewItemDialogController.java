package com.lecture197.todolist;

import com.lecture197.todolist.datamodel.TodoData;
import com.lecture197.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class NewItemDialogController {
    @FXML
    private TextField shortDescTextField;
    @FXML
    private TextArea detailsTextArea;
    @FXML
    private DatePicker deadlineDatePicker;

    public TodoItem processForm() {
        String shortDesc = this.shortDescTextField.getText().trim();
        String details = this.detailsTextArea.getText().trim();
        LocalDate deadline = this.deadlineDatePicker.getValue();

        TodoItem newItem = new TodoItem(shortDesc, details, deadline);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }
}
