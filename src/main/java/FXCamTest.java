import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FXCamTest extends Application {

    public Webcam cam;
	public WebCamService service ;

	@Override
	public void init() {
		
		// note this is in init as it **must not** be called on the FX Application Thread:

		cam = Webcam.getWebcams().get(0);
		service = new WebCamService(cam);	
	}



	@Override
	public void start(Stage primaryStage) {

		Button startStop = new Button();
		startStop.textProperty().bind(Bindings.
				when(service.runningProperty()).
				then("Stop").
				otherwise("Start"));
		
		startStop.setOnAction(e -> {
			if (service.isRunning()) {
				service.cancel();
			} else {
				service.restart();
			}
		});
		
		WebCamView view = new WebCamView(service);
		Button btna=new Button("capture");
		btna.setOnAction(this::actionPerformed);


		FlowPane root = new FlowPane(10,10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(view.getView(),startStop,btna);
		
		Scene scene = new Scene(root,300,300);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

    public void actionPerformed(ActionEvent ae) {
        // get image

        BufferedImage image = cam.getImage();
        // save image to PNG file
        try {
            ImageIO.write(image, "PNG", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            //captureDisplay(image);
            cam.close();
        }
    }

	public static void main(String[] args) {
		launch(args);
	}
}