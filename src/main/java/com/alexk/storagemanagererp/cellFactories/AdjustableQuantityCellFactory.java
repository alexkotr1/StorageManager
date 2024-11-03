package com.alexk.storagemanagererp.cellFactories;

import com.alexk.storagemanagererp.Helper;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import com.alexk.storagemanagererp.storage.SaleStorageItem;


public class AdjustableQuantityCellFactory implements Callback<TableColumn<SaleStorageItem, Integer>, TableCell<SaleStorageItem, Integer>> {

    private TableView<SaleStorageItem> tableView; // Store the TableView instance

    public AdjustableQuantityCellFactory(TableView<SaleStorageItem> tableView) {
        this.tableView = tableView;
    }

    @Override
    public TableCell<SaleStorageItem, Integer> call(TableColumn<SaleStorageItem, Integer> column) {
        return new TableCell<SaleStorageItem, Integer>() {
            private HBox container;
            private Button minusButton;
            private Label quantityLabel;
            private Button plusButton;
            private Boolean isPlusPressed = false;
            private Boolean isMinusPressed = false;
            private Boolean timelineAdded = false;
            private Boolean timelineRemoved = false;
            private Timeline timeline = new Timeline();


            {
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.setAutoReverse(false);

                    container = new HBox();
                    container.setAlignment(Pos.CENTER);

                    minusButton = new Button("-");

                    quantityLabel = new Label("1");
                    quantityLabel.setStyle("-fx-font-size: 14px;");

                    plusButton = new Button("+");
                    setText(null);
                    setGraphic(null);
                    container.setSpacing(10);
                    container.getChildren().addAll(minusButton, quantityLabel, plusButton);
                    minusButton.setOnMousePressed(event -> {
                        if (!isEmpty()) {
                            minusButtonPressed();
                        }
                    });

                    minusButton.setOnMouseReleased(event -> {
                        if (!isEmpty()) {
                            minusButtonReleased();
                        }
                    });

                    plusButton.setOnMousePressed(event -> {
                        if (!isEmpty()) {
                            plusButtonPressed();
                        }
                    });

                    plusButton.setOnMouseReleased(event -> {
                        if (!isEmpty()) {
                            plusButtonReleased();
                        }
                    });
                }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (item != null && shouldAdd(getIndex())){
                    quantityLabel.setText(item.toString());
                    setGraphic(container);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                setGraphic(container);
                setText(null);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setGraphic(container);
                setText(null);
            }

            @Override
            public void commitEdit(Integer newValue) {
                super.commitEdit(newValue);
                setText(null);
                    quantityLabel.setText(newValue.toString());
                    setItem(newValue);
                    SaleStorageItem saleStorageItem = tableView.getItems().get(getIndex());
                    saleStorageItem.setTimesInCheckout(newValue);
            }

            private void minusButtonPressed() {
                isMinusPressed = true;
                timeline.stop();
                timeline.getKeyFrames().clear();
                KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
                    if (isMinusPressed) {
                        timelineRemoved = true;
                        decrementValue();
                    }
                    else timeline.stop();
                });
                timeline.getKeyFrames().add(keyFrame);
                timeline.play();
            }

            private void minusButtonReleased() {
                if (!timelineAdded){
                    decrementValue();
                }
                timelineRemoved = false;
                isMinusPressed = false;
            }

            private void plusButtonPressed() {
                isPlusPressed = true;
                timeline.stop();
                timeline.getKeyFrames().clear();
                KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
                    if (isPlusPressed) {
                        timelineAdded = true;
                        incrementValue();
                    }
                    else timeline.stop();
                });
                timeline.getKeyFrames().add(keyFrame);
                timeline.play();
            }

            private void plusButtonReleased() {
                if (!timelineAdded){
                    incrementValue();
                }
                timelineAdded = false;
                isPlusPressed = false;
            }

            private void decrementValue() {
                if (getItem() == 1){
                    Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Η ποσότητα δεν μπορεί να είναι μηδενική!");
                    return;
                }
                commitEdit(getItem() - 1);
            }

            private Boolean shouldAdd(Integer index){
                return tableView.getItems().get(index).getItemName() != null;
            }

            private void incrementValue() {
                commitEdit(getItem() + 1);
            }
        };
    }
}

