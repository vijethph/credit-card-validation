/**
 * using ByteDeco Java wrapper of Tesseract OCR
 * @author Vijeth P H
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.TessBaseAPI;




/**
 * @author Vijeth PH
 *
 */
public class ByteDecoOCR{
    public Boolean flag=true;

    public FlowPane tessResults(){



        BytePointer ot;
        TessBaseAPI api=new TessBaseAPI();
        if(api.Init("/usr/local/share/tessdata","eng")!=0) {
            System.err.println("Could not initialize tesseract");
            System.exit(1);
        }
        PIX img= pixRead("test.png");

        api.SetImage(img);
        ot=api.GetUTF8Text();
        String str=ot.getString();

        api.End();
        ot.deallocate();
        pixDestroy(img);

        Label lb1=new Label("Result: ");
        lb1.setStyle("-fx-font-size: 20");
        Label lb2=new Label(str);
        lb2.setStyle("-fx-font-size: 30");
        Label lb3=new Label("Is this correct?");
        lb3.setStyle("-fx-font-size: 20");
        Button btna=new Button("Yes, it is");
        btna.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                creditCardScene(actionEvent);
            }
        });
        Button btnb=new Button("No, it isn't");
        btnb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("button clicked");
                flag=false;
                creditCardScene(actionEvent);
            }
        });
        //System.out.println("in this class");
        TextField tf=new TextField(str);
        FlowPane flowPane2=new FlowPane(10,10);
        flowPane2.setAlignment(Pos.CENTER);
        flowPane2.setOrientation(Orientation.VERTICAL);
        flowPane2.getChildren().addAll(lb1,lb2,lb3,btna,btnb);
        flowPane2.setStyle("-fx-background-color: #F0F0F0");
        return flowPane2;
    }

    public void creditCardScene(ActionEvent actionEvent){
        CreditCardDisplay mds=new CreditCardDisplay();
        Scene konedu=new Scene(mds.numberValidation(flag),500,500);
        Stage thisStage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        thisStage.setTitle("Credit Card Validation");
        thisStage.setScene(konedu);
    }
}

