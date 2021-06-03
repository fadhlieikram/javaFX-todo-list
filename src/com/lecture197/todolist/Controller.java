package com.lecture197.todolist;

import com.lecture197.todolist.datamodel.TodoData;
import com.lecture197.todolist.datamodel.TodoItem;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
    @FXML
    private BorderPane mainBorderPain;
    @FXML
    private ListView<TodoItem> itemListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private ContextMenu listViewContectMenu;

    public void initialize() throws  Exception{
        this.listViewContectMenu = new ContextMenu();
        MenuItem deleteMenuItem =  new MenuItem("Delete");
        deleteMenuItem.setOnAction(e -> {
            TodoItem item = this.itemListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });
        listViewContectMenu.getItems().add(deleteMenuItem);

        if(TodoData.isDataFileExist()) {
            TodoData.getInstance().loadTodoItems();
        }

        // Listener for itemListview
        this.itemListView.getSelectionModel().selectedItemProperty().addListener((observable, todoItem, newTodoItem) -> {
            if (newTodoItem != null) {
                TodoItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                itemDetailsTextArea.setText(selectedItem.getDetails());
                deadlineLabel.setText(df.format(selectedItem.getDeadLine()));
            }
        });
        // Set behavior of each cells in itemListView
        this.itemListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean empty) {
                        super.updateItem(todoItem, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(todoItem.getShortDesc());
                            if (todoItem.getDeadLine().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDeadLine().isEqual(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.YELLOW);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(listViewContectMenu);
                    }
                });
                return cell ;
            }
        });
        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(), (o1, o2) -> {
            return o1.getDeadLine().compareTo(o2.getDeadLine());
        });
        this.itemListView.setItems(sortedList);
        this.itemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Select first item bv default
        this.itemListView.getSelectionModel().selectFirst();
    }

    private void deleteItem(TodoItem item) {
        System.out.println("deleting " + item);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete '" + item.getShortDesc() + "' ?");
        alert.setContentText("Hit 'OK' if you want to proceed.");
        Optional<ButtonType> res = alert.showAndWait();

        if (res.isPresent() && res.get().equals(ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(this.mainBorderPain.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newitemidialog.fxml"));
        dialog.setTitle("New todo");

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load new item dialogue");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> res = dialog.showAndWait();

        if(res.isPresent() && res.get().equals(ButtonType.OK)) {
            System.out.println("OK clicked");
            NewItemDialogController dialogController = fxmlLoader.getController();
            TodoItem newItem = dialogController.processForm();
            this.itemListView.getSelectionModel().select(newItem);

        } else {
            // close the dialog and do nothing
            System.out.println("Cancelled clicked");
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent e) {
        TodoItem item = this.itemListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (e.getCode().equals(KeyCode.DELETE)) {
                deleteItem(item);
            }
        }
    }

}
