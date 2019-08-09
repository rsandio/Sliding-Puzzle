import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main extends Application {

    static Stage mainStage;

    static VBox mainLayout = new VBox();
    static GridPane gridLayout;

    static Image image = new Image("dolphin.png");

    static int numTiles = 9;
    static Tile[][] tiles;

    public static void main(String[] args){
        launch();
    }



    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage;

        refresh();

        Scene mainScene = new Scene(mainLayout);

        mainStage.setScene(mainScene);
        mainStage.setResizable(false);
        mainStage.setTitle("Puzzle Slider");
        mainStage.show();
    }

    private static void refresh(){
        tiles = new Tile[(int) Math.sqrt(numTiles)][(int) Math.sqrt(numTiles)];
        mainLayout.getChildren().clear();
        mainLayout.getChildren().addAll(createMenu(), createGrid());

        // Mix it up
        Random rand = new Random();
        for(int i = 0; i < 10000; i++){
            int x = rand.nextInt((int) Math.sqrt(numTiles));
            int y = rand.nextInt((int) Math.sqrt(numTiles));
            tiles[x][y].clicked(true);
        }
    }

    private static Parent createGrid() {
        for(int i = 0; i < (int) Math.sqrt(numTiles); i++){
            for(int j = 0; j < (int) Math.sqrt(numTiles); j++){
                PixelReader reader = image.getPixelReader();
                WritableImage newImage = new WritableImage(reader, i*(480/(int) Math.sqrt(numTiles)),j*(480/(int) Math.sqrt(numTiles)),(480/(int) Math.sqrt(numTiles)),(480/(int) Math.sqrt(numTiles)));

                Tile tile;

                if(i == (int) Math.sqrt(numTiles)-1 && j == (int) Math.sqrt(numTiles)-1) {
                    tile = new Tile(newImage, true,i,j);
                }
                else{
                    tile = new Tile(newImage, false,i,j);
                }
                tiles[i][j] = tile;
            }
        }

        gridLayout = new GridPane();
        gridLayout.setHgap(1);
        gridLayout.setVgap(1);

        for(int i = 0; i < (int) Math.sqrt(numTiles); i++){
            for(int j = 0; j < (int) Math.sqrt(numTiles); j++) {
                gridLayout.add(tiles[i][j],i,j);
            }
        }
        return gridLayout;
    }

    private static Node createMenu(){

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem aboutMenuItem = new MenuItem("About");

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> mainStage.close());

        fileMenu.getItems().addAll(aboutMenuItem,exitMenuItem);

        Menu imageMenu = new Menu("Image");

        MenuItem parrotImageMenuItem = new MenuItem("Parrot");
        parrotImageMenuItem.setOnAction(actionEvent -> {image = new Image("parrot.png"); refresh();});
        MenuItem dolphinImageMenuItem = new MenuItem("Dolphin");
        dolphinImageMenuItem.setOnAction(actionEvent -> {image = new Image("dolphin.png"); refresh();});
        MenuItem jellyBeanImageMenuItem = new MenuItem("Jelly Beans");
        jellyBeanImageMenuItem.setOnAction(actionEvent -> {image = new Image("jellyBeans.png"); refresh();});
        MenuItem customImageMenuItem = new MenuItem("Custom...");
        customImageMenuItem.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.bmp", "*.png")
            );
            File selectedFile = fileChooser.showOpenDialog(mainStage);
            try {
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                image = SwingFXUtils.toFXImage(bufferedImage, null);
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        imageMenu.getItems().addAll(dolphinImageMenuItem, parrotImageMenuItem, jellyBeanImageMenuItem, customImageMenuItem);

        Menu difficultyMenu = new Menu("Difficulty");

        MenuItem easyMenuItem = new MenuItem("Easy (3x3)");
        easyMenuItem.setOnAction(actionEvent -> { numTiles = 9; refresh(); });
        MenuItem mediumMenuItem = new MenuItem("Medium (4x4)");
        mediumMenuItem.setOnAction(actionEvent -> { numTiles = 16; refresh(); });
        MenuItem hardMenuItem = new MenuItem("Hard (5x5)");
        hardMenuItem.setOnAction(actionEvent -> { numTiles = 20; refresh(); });
        MenuItem insaneMenuItem = new MenuItem("Insane (8x8)");
        insaneMenuItem.setOnAction(actionEvent -> { numTiles = 64; refresh(); });

        difficultyMenu.getItems().addAll(easyMenuItem,mediumMenuItem,hardMenuItem,insaneMenuItem);

        menuBar.getMenus().addAll(fileMenu, imageMenu, difficultyMenu);

        return menuBar;
    }

    static void checkWin() {
        int counter = 0;
        for(int i = 0; i < Main.tiles.length; i++){
            for(int j = 0; j < Main.tiles[i].length; j++){
                if(tiles[i][j].isOriginalBackground()){
                    counter++;
                }
            }
        }
        if(counter == Main.numTiles){
            won();
        }
    }

    static void won(){
        Alert win = new Alert(Alert.AlertType.CONFIRMATION);
        win.setTitle("Win!");
        win.setHeaderText("Congratulations!");
        win.setContentText("You solved the puzzle. You are quite literally a genius! Get rekt!");
        win.showAndWait();
        refresh();
    }
}
