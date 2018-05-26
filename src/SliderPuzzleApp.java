import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class SliderPuzzleApp extends Application {
    public String puzzleType;
    public int rows, cols, gridD;
    public Timeline updateTimer;
    public int min, sec;

    public Pane mainPane;
    public GridPane gamePane;
    public Pane sidePane;

    public Button images[][];

    public void start(Stage primaryStage) {
        mainPane = new Pane();
        gamePane = new GridPane();
        sidePane = new Pane();

        puzzleType = null;
        rows = 4;
        cols = 4;
        gridD = 0;

        ListView<String> gameOptions = new ListView<String>();
        String puzzleOptions[] = {"Lego", "Numbers", "Pets", "Scenery"};
        ObservableList<String> puzzleList = FXCollections.observableArrayList(puzzleOptions);
        gameOptions.setItems(puzzleList);
        gameOptions.setPrefSize(187, 187);

        gameOptions.setPrefWidth(187);

        //Timer Button
        Button timer = new Button("Start");
        timer.setPrefSize(187, 40);
        timer.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white");

        //Label for the thumbnail
        Label smallImg = new Label();

        //Label for time
        Label timeLabel = new Label("Time:");

        //Textflied to the time
        TextField timeField = new TextField("0:00");

        timeField.setEditable(false);

        gamePane.addColumn(cols);
        gamePane.addRow(rows);

        gamePane.setHgap(1);
        gamePane.setVgap(1);

        images = new Button[rows][cols];

        smallImg.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));

        //Populate the buttons
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                images[i][j] = new Button();
                images[i][j].setPrefSize(187, 187);
                images[i][j].setPadding(new Insets(0,0,0,0));
                images[i][j].setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
                gamePane.add(images[i][j], j, i);
            }
        }

        //Initializing listview
        gameOptions.getSelectionModel().selectFirst();

        //Event handlers
        gameOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                puzzleType = gameOptions.getSelectionModel().getSelectedItem();
                smallImg.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(puzzleType + "_Thumbnail" + ".png"))));
            }
        });

        timer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (timer.getText().equals("Start")) {
                    smallImg.setDisable(true);
                    gameOptions.setDisable(true);

                    timer.setText("Stop");
                    timer.setStyle("-fx-background-color: darkred; -fx-text-fill: white");

                    //Random goes here
                    setUpGame();

                    shuffle();

                    updateTimer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event) {
                            // FILL IN YOUR CODE HERE THAT WILL GET CALLED ONCE PER SEC.
                            if (sec == 60) {
                                min++;
                                sec = 0;
                            }

                            timeField.setText(String.format("%d:%02d", min, sec));

                            if (isComplete() >= 15) {
                                timer.fire();
                                setUpGame();

                                for (int i = 0; i < 4; i++) {
                                    for (int j = 0; j < 4; j++) {
                                        images[i][j].setDisable(true);
                                    }
                                }
                            }

                            sec++;
                        }
                    }));

                    updateTimer.setCycleCount(Timeline.INDEFINITE);
                    updateTimer.play();
                } else if (timer.getText().equals("Stop")) {
                    smallImg.setDisable(false);
                    gameOptions.setDisable(false);

                    smallImg.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(puzzleType + "_Thumbnail" + ".png"))));

                    setUpBlank();

                    timer.setText("Start");
                    timer.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white");

                    timeField.setText("0:00");
                    updateTimer.stop();
                    min = 0;
                    sec = 0;
                }
            }
        });

        //Checking for mouse click in a certain location in the grid
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                images[i][j].setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        for (int r = 0; r < rows; r++) {
                            for (int c = 0; c < cols; c++) {
                                if (event.getSource() == images[r][c]){
                                    swap(r, c);
                                    if (isComplete() >= 15) {
                                        timer.fire();
                                        setUpGame();

                                        for (int i = 0; i < 4; i++) {
                                            for (int j = 0; j < 4; j++) {
                                                images[i][j].setDisable(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        //Add stuff to the side pane
        sidePane.setPadding(new Insets(0, 0, 5, 5));
        sidePane.getChildren().addAll(gameOptions, smallImg, timer, timeLabel, timeField);
        sidePane.relocate(760, 5);
        smallImg.relocate(0, 0);
        gameOptions.relocate(0, 197);
        timer.relocate(0, 394);
        timeLabel.relocate(0, 446);
        timeField.relocate(39, 444);

        //Add everything to the main pane
        gamePane.setPadding(new Insets(5, 5, 5, 5));
        mainPane.getChildren().addAll(gamePane, sidePane);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Slider Puzzle Game");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    public void swap (int r, int c) {
        Button tempB = new Button();
        String tempId = "";

        if ((r + 1 < 4) && images[r + 1][c].getId() == null) {
            tempB.setGraphic(images[r][c].getGraphic());
            tempId = images[r][c].getId();

            images[r][c].setId(images[r + 1][c].getId());
            images[r][c].setGraphic(images[r + 1][c].getGraphic());

            images[r + 1][c].setId(tempId);
            images[r + 1][c].setGraphic(tempB.getGraphic());

        } else if ((r - 1 >= 0) && images[r - 1][c].getId() == null) {
            tempB.setGraphic(images[r][c].getGraphic());
            tempId = images[r][c].getId();

            images[r][c].setId(images[r - 1][c].getId());
            images[r][c].setGraphic(images[r - 1][c].getGraphic());

            images[r - 1][c].setId(tempId);
            images[r - 1][c].setGraphic(tempB.getGraphic());

        } else if ((c + 1 < 4) && images[r][c + 1].getId() == null) {
            tempB.setGraphic(images[r][c].getGraphic());
            tempId = images[r][c].getId();

            images[r][c].setId(images[r][c + 1].getId());
            images[r][c].setGraphic(images[r][c + 1].getGraphic());

            images[r][c + 1].setId(tempId);
            images[r][c + 1].setGraphic(tempB.getGraphic());

        } else if ((c - 1 >= 0) && images[r][c - 1].getId() == null) {
            tempB.setGraphic(images[r][c].getGraphic());
            tempId = images[r][c].getId();

            images[r][c].setId(images[r][c - 1].getId());
            images[r][c].setGraphic(images[r][c - 1].getGraphic());

            images[r][c - 1].setId(tempId);
            images[r][c - 1].setGraphic(tempB.getGraphic());

        }
    }

    public void swap (int r, int c) {
        Button tempB = new Button();
        String tempId = "";

        int direction = 0;
        int r2 = r;
        int c2 = c;

        if ((r + 1 < 4) && images[r + 1][c].getId() == null) {
            direction = 1;
        } else if ((r - 1 >= 0) && images[r - 1][c].getId() == null) {
            direction = 2;
        } else if ((c + 1 < 4) && images[r][c + 1].getId() == null) {
            direction = 3;
        } else if ((c - 1 >= 0) && images[r][c - 1].getId() == null) {
            direction = 4;
        }

        tempB.setGraphic(images[r][c].getGraphic());
        tempId = images[r][c].getId();

        switch (direction) {
            case 1:
                r2 = r + 1;
            case 2:
                r2 = r - 1;
            case 3:
                c2 = c + 1;
            case 4:
                c2 = c - 1;
        }

        images[r][c].setId(images[r2][c2].getId());
        images[r][c].setGraphic(images[r2][c2].getGraphic());

        images[r2][c2].setId(tempId);
        images[r2][c2].setGraphic(tempB.getGraphic());
    }

    public void shuffle() {
        Random rand = new Random();

        int blankX = rand.nextInt(4);
        int blankY = rand.nextInt(4);

        images[blankX][blankY].setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
        images[blankX][blankY].setId(null);

        for (int i = 0; i < 5000; i ++) {
            int randY = rand.nextInt(4);
            int randX = rand.nextInt(4);

            swap(randX, randY);
        }
    }

    public void setUpBlank() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                images[i][j].setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
                images[i][j].setId(null);
            }
        }
    }

    public void setUpGame() {
        for (int i = 0; i < rows; i++) {
            gridD = 10 * i;
            for (int j = 0; j < cols; j++) {
                images[i][j].setGraphic(new ImageView(new Image(getClass().getResourceAsStream(puzzleType + "_" + String.format("%02d", gridD) + ".png"))));
                images[i][j].setId(puzzleType + "_" + String.format("%02d", gridD) + ".png");
                images[i][j].setDisable(false);
                gridD++;
            }
        }
    }

    public int isComplete() {
        int score = 0;

        for (int i = 0; i < rows; i++) {
            gridD = 10 * i;
            for (int j = 0; j < cols; j++) {
                if ((images[i][j].getId() != null) && images[i][j].getId().equals(puzzleType + "_" + String.format("%02d", gridD) + ".png")) {
                    score++;
                }

                gridD++;
            }
        }

        return score;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
