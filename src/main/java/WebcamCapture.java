/*
 Done By SilentKiller
 */
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


/**
 * @author Vijeth PH
 */
public class WebcamCapture extends Application {

    Stage window;
    Scene sc1,sc2;

    @Override
    public void start(Stage stage) throws Exception{

        Webcam webcam=Webcam.getDefault();
        Dimension[] none=new Dimension[] {
                WebcamResolution.PAL.getSize(),WebcamResolution.HD.getSize(),new Dimension(2000,1000),new Dimension(1000,500)
        };
        webcam.setCustomViewSizes(none);
        webcam.setViewSize(WebcamResolution.HD.getSize());
        webcam.open();



        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);


        window=stage;
        window.setTitle("Webcam Capture");
        GridPane gridPane=new GridPane();
        gridPane.setMinSize(800,600);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        Button btn = new Button("Capture");
        btn.setOnAction(this::actionPerformed);
        Separator sp=new Separator();
        sp.setPrefWidth(420);
        gridPane.add(btn,0,0);
        gridPane.add(sp,1,1);
        gridPane.add(btn,1,2);


    }
    public void actionPerformed(ActionEvent ae) {
        // get image
        Webcam wb=Webcam.getDefault();
        BufferedImage image = wb.getImage();
        // save image to PNG file
        try {
            ImageIO.write(image, "PNG", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            //captureDisplay(image);
            wb.close();
        }
    }
    /*
    public void captureDisplay(BufferedImage image) {
        JFrame jf = new JFrame("Result");
        JLabel jl = new JLabel(new ImageIcon(image));
        jf.setLayout(new FlowLayout());
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(jl);
        jf.setResizable(true);
        jf.pack();
        jf.setVisible(true);

    }


     */
    public static void main(String[] args) throws IOException {
        launch(args);

        /*
        Webcam webcam = Webcam.getDefault();
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        new WebcamCapture(webcam);
                    }
                });



        webcam.close();


         */
    }

}
