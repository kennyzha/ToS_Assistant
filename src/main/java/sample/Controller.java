package sample;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    private HashMap<String, MutableInt> toggleTracker;

    @FXML AnchorPane anchorPane;
    @FXML ImageView imageView;
    @FXML TableView<Player> playerTableView;
    @FXML TableColumn<Player, String> tableNumCol;
    @FXML TableColumn<Player, String> tableNameCol;
    @FXML TableColumn<Player, String> tableRoleCol;


    public Controller(){
        toggleTracker = new HashMap<>();
    }

    @FXML
    public void initialize(){
        System.out.println("Initializing");
        tableNumCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        tableNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableNumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        tableNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        tableRoleCol.setCellFactory(TextFieldTableCell.forTableColumn());

        ObservableList<Player> emptyPlayers = FXCollections.observableArrayList();
        emptyPlayers.add(new Player("","",""));

        playerTableView.setItems(emptyPlayers);
        playerTableView.setEditable(true);
    }

    @FXML
    public void handle(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Button) { //should always be true in your example
            Button clickedBtn = (Button) source; // that's the button that was clicked
            String btnId = clickedBtn.getId();

            if(toggleTracker.containsKey(btnId)){
                if(toggleTracker.get(btnId).getValue() % 2 == 0){
                    clickedBtn.setStyle("-fx-background-color: #990000");
                } else{
                    clickedBtn.setStyle("");
                }
                toggleTracker.get(btnId).incrementValue();
            }else{
                toggleTracker.put(btnId, new MutableInt(1));
                clickedBtn.setStyle("-fx-background-color: #990000");
            }
        }
    }
    @FXML
    public void mouseDragDropped(final DragEvent e) {
        System.out.println("Drag Dropped detected");
        final Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            // Only get the first file from the list
            final File file = db.getFiles().get(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println(file.getAbsolutePath());
                    try {
                        String[] rawPlayerNames = detectText(file.getAbsolutePath(), System.out);
                        parsePlayerNames(rawPlayerNames);
                        playerTableView.setItems(getPlayers(rawPlayerNames));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        System.out.println("mouse drag dropped");
        e.setDropCompleted(success);
        e.consume();
    }

    @FXML
    public void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");

        if (db.hasFiles()) {
            if (isAccepted) {
                anchorPane.setStyle("-fx-border-color: red;"
                        + "-fx-border-width: 5;"
                        + "-fx-background-color: #C6C6C6;"
                        + "-fx-border-style: solid;");
                e.acceptTransferModes(TransferMode.COPY);
            }
        } else {
            e.consume();
        }
    }

    public ObservableList<Player> getPlayers(String[] playerNames){
        ObservableList<Player> players = FXCollections.observableArrayList();

        for(int i = 0; i < playerNames.length; i++){
            int curPlayerNum = i+1;
            players.add(new Player( Integer.toString(curPlayerNum), playerNames[i], ""));
        }
        return  players;
    }

    public static String[] detectText(String filePath, PrintStream out) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        BatchAnnotateImagesResponse response =
                ImageAnnotatorClient.create().batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        String[] rawPlayerNames;
        if(responses != null && responses.size() > 0){
            rawPlayerNames = responses.get(0).getTextAnnotationsList().get(0).getDescription().split("\n");

            for(int i = 0; i < rawPlayerNames.length; i++){
                out.println(rawPlayerNames[i]);
            }
            return rawPlayerNames;
        }
        return new String[0];
    }

    public String[] parsePlayerNames(String[] rawPlayerNames){
        return null;
    }
}
