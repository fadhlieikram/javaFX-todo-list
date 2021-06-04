package com.lecture197.todolist;

import com.lecture197.todolist.datamodel.TodoData;
import com.lecture197.todolist.datamodel.TodoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private ContextMenu listViewContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    // Create app data file to save list if doesn't exist
    public void initialize() throws  Exception{
        if(TodoData.isDataFileExist()) {
            TodoData.getInstance().loadTodoItems();
        }

        // Set listener for itemListview to update TextArea and deadline Label when an item is selected
        this.itemListView.getSelectionModel().selectedItemProperty().addListener((observable, oldTodoItem, newTodoItem) -> {
            if (newTodoItem != null) {
                TodoItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                itemDetailsTextArea.setText(selectedItem.getDetails());
                deadlineLabel.setText(df.format(selectedItem.getDeadLine()));
            }
        });
        // Set ContextMenu for itemListView and add 'Delete' option
        this.listViewContextMenu = new ContextMenu();
        MenuItem deleteMenuItem =  new MenuItem("Delete");
        deleteMenuItem.setOnAction(e -> {
            TodoItem item = this.itemListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });
        listViewContextMenu.getItems().add(deleteMenuItem);
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
                        cell.setContextMenu(listViewContextMenu);
                    }
                });
                return cell ;
            }
        });
        SortedList<TodoItem> sortedList = new SortedList<>(TodoData.getInstance().getTodoItems(), (o1, o2) -> {
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

    @FXML
    public void handleFiltering() {
        TodoItem selectedItem = this.itemListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {

            ObservableList<TodoItem> filteredItems = this.itemListView.getItems().stream()
                    .filter(todoItem -> LocalDate.now().isEqual(todoItem.getDeadLine()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            this.itemListView.setItems(filteredItems);
            if (filteredItems.isEmpty()) {
                this.itemDetailsTextArea.clear();
                this.deadlineLabel.setText("");
            } else if (filteredItems.contains(selectedItem)) {
                this.itemListView.getSelectionModel().select(selectedItem);
            } else {
                this.itemListView.getSelectionModel().selectFirst();
            }
        } else {
            SortedList<TodoItem> sortedList = new SortedList<>(TodoData.getInstance().getTodoItems(),
                    Comparator.comparing(TodoItem::getDeadLine));
            this.itemListView.setItems(sortedList);
            this.itemListView.getSelectionModel().select(selectedItem);
        }
    }

}
