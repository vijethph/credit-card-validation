import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This demonstrates how to use Webcam Capture API in a JavaFX application.
 * Further modified to suit application purpose
 * @author Rakesh Bhatt (rakeshbhatt10)
 * @author Vijeth P H (vijethph)
 */
public class WebCamAppLauncher extends Application {

	public class WebCamInfo {

		public String webCamName;
		public int webCamIndex;

		public String getWebCamName() {
			return webCamName;
		}

		public void setWebCamName(String webCamName) {
			this.webCamName = webCamName;
		}

		public int getWebCamIndex() {
			return webCamIndex;
		}

		public void setWebCamIndex(int webCamIndex) {
			this.webCamIndex = webCamIndex;
		}

		@Override
		public String toString() {
			return webCamName;
		}
	}

	public Stage window;
	public Scene scene2;
	public FlowPane bottomCameraControlPane;
	public FlowPane topPane;
	public BorderPane root;
	public String cameraListPromptText = "Choose Camera";
	public ImageView imgWebCamCapturedImage;
	public Webcam webCam = null;
	public boolean stopCamera = false;
	public BufferedImage grabbedImage;
	public ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	public BorderPane webCamPane;
	public Button btnCameraStop;
	public Button btnCameraStart;
	public Button btnCameraDispose;
	public Button btnCameraCapture;
	
	@Override
	public void start(Stage primaryStage) {

		window=primaryStage;
		window.setTitle("Connecting Camera Device");

		root = new BorderPane();
		topPane = new FlowPane();
		topPane.setAlignment(Pos.CENTER);
		topPane.setHgap(20);
		topPane.setOrientation(Orientation.HORIZONTAL);
		topPane.setPrefHeight(40);
		root.setTop(topPane);
		webCamPane = new BorderPane();
		webCamPane.setStyle("-fx-background-color: #ccc;");
		imgWebCamCapturedImage = new ImageView();
		webCamPane.setCenter(imgWebCamCapturedImage);
		root.setCenter(webCamPane);
		createTopPanel();
		bottomCameraControlPane = new FlowPane();
		bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
		bottomCameraControlPane.setAlignment(Pos.CENTER);
		bottomCameraControlPane.setHgap(20);
		bottomCameraControlPane.setVgap(10);
		bottomCameraControlPane.setPrefHeight(40);
		bottomCameraControlPane.setDisable(true);
		createCameraControls();
		root.setBottom(bottomCameraControlPane);

		window.setScene(new Scene(root));
		window.setHeight(700);
		window.setWidth(600);
		window.centerOnScreen();
		window.show();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				setImageViewSize();
			}
		});

	}

	protected void setImageViewSize() {

		double height = webCamPane.getHeight();
		double width = webCamPane.getWidth();

		imgWebCamCapturedImage.setFitHeight(height);
		imgWebCamCapturedImage.setFitWidth(width);
		imgWebCamCapturedImage.prefHeight(height);
		imgWebCamCapturedImage.prefWidth(width);
		imgWebCamCapturedImage.setPreserveRatio(true);

	}

	public void createTopPanel() {

		int webCamCounter = 0;
		Label lbInfoLabel = new Label("Select Your WebCam Camera");
		ObservableList<WebCamInfo> options = FXCollections.observableArrayList();

		topPane.getChildren().add(lbInfoLabel);

		for (Webcam webcam : Webcam.getWebcams()) {
			WebCamInfo webCamInfo = new WebCamInfo();
			webCamInfo.setWebCamIndex(webCamCounter);
			webCamInfo.setWebCamName(webcam.getName());
			options.add(webCamInfo);
			webCamCounter++;
		}

		ComboBox<WebCamInfo> cameraOptions = new ComboBox<WebCamInfo>();
		cameraOptions.setItems(options);
		cameraOptions.setPromptText(cameraListPromptText);
		cameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

			@Override
			public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
				if (arg2 != null) {
					System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
					initializeWebCam(arg2.getWebCamIndex());
				}
			}
		});
		topPane.getChildren().add(cameraOptions);
	}

	protected void initializeWebCam(final int webCamIndex) {

		Task<Void> webCamTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				if (webCam != null) {
					disposeWebCamCamera();
				}

				webCam = Webcam.getWebcams().get(webCamIndex);
				Dimension[] dms=new Dimension[]{
						WebcamResolution.PAL.getSize(),WebcamResolution.HD.getSize(),new Dimension(2000,1000),new Dimension(1000,500)
				};
				webCam.setCustomViewSizes(dms);
				webCam.setViewSize(WebcamResolution.HD.getSize());
				webCam.open();

				startWebCamStream();

				return null;
			}
		};

		Thread webCamThread = new Thread(webCamTask);
		webCamThread.setDaemon(true);
		webCamThread.start();

		bottomCameraControlPane.setDisable(false);
		btnCameraStart.setDisable(true);
	}

	protected void startWebCamStream() {

		stopCamera = false;

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				final AtomicReference<WritableImage> ref = new AtomicReference<>();
				BufferedImage img = null;

				while (!stopCamera) {
					try {
						if ((img = webCam.getImage()) != null) {

							ref.set(SwingFXUtils.toFXImage(img, ref.get()));
							img.flush();

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageProperty.set(ref.get());
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return null;
			}
		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		imgWebCamCapturedImage.imageProperty().bind(imageProperty);

	}

	public void createCameraControls() {

		btnCameraStop = new Button();
		btnCameraStop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				stopWebCamCamera();
			}
		});
		btnCameraStop.setText("Stop Camera");
		btnCameraStart = new Button();
		btnCameraStart.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				startWebCamCamera();
			}
		});
		btnCameraStart.setText("Start Camera");
		btnCameraDispose = new Button();
		btnCameraDispose.setText("Dispose Camera");
		btnCameraDispose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				disposeWebCamCamera();
			}
		});

		btnCameraCapture = new Button();
		btnCameraCapture.setText("Capture Camera");
		btnCameraCapture.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				captureWebCamCamera();
			}
		});


		bottomCameraControlPane.getChildren().add(btnCameraStart);
		bottomCameraControlPane.getChildren().add(btnCameraStop);
		bottomCameraControlPane.getChildren().add(btnCameraCapture);
		bottomCameraControlPane.getChildren().add(btnCameraDispose);
	}

	protected void disposeWebCamCamera() {
		stopCamera = true;
		webCam.close();
		btnCameraStart.setDisable(true);
		btnCameraStop.setDisable(true);
	}

	protected void startWebCamCamera() {
		stopCamera = false;
		startWebCamStream();
		btnCameraStop.setDisable(false);
		btnCameraStart.setDisable(true);
	}

	protected void captureWebCamCamera() {
		grabbedImage=webCam.getImage();
		try {
			ImageIO.write(grabbedImage, "PNG", new File("test.png"));
		} catch (IOException e) {
			e.printStackTrace();

		}
		stopCamera = true;
		webCam.close();
		btnCameraStart.setDisable(false);
		btnCameraStop.setDisable(true);
		Label txtlabel=new Label("Captured Image");
		ImageView imgv=new ImageView(SwingFXUtils.toFXImage(grabbedImage,null));
		imgv.setFitHeight(500);
		imgv.setFitWidth(400);
		imgv.setPreserveRatio(true);
		Button btn=new Button("Perform OCR");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				//System.out.println("before ocr button");
				ByteDecoOCR btr=new ByteDecoOCR();
				Scene another=new Scene(btr.tessResults(),500,500);
				window.setScene(another);
				//System.out.println("after change scene");
			}
		});
		//System.out.println("before primary");
		FlowPane flowPane=new FlowPane(10,10);
		flowPane.setPadding(new Insets(10));
		flowPane.setOrientation(Orientation.VERTICAL);
		flowPane.setAlignment(Pos.CENTER);
		flowPane.getChildren().addAll(txtlabel,imgv,btn);
		scene2=new Scene(flowPane,600,300);

		window.setScene(scene2);
		//System.out.println("after primary");
	}
	protected void stopWebCamCamera() {
		stopCamera = true;
		btnCameraStart.setDisable(false);
		btnCameraStop.setDisable(true);
	}

	public static void main(String[] args) {
		launch(args);
	}
}