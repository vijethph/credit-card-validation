/**
 * using javafx graphics
 * @author Vijeth P H
 */
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class CreditCardDisplay {
    public FlowPane numberValidation(Boolean flag,String str){
        Label label1=new Label("Credit Card Number: ");
        TextField textField=new TextField(str);
        Label label2=new Label();
        Label label3=new Label();
        if(!flag){
            label3.setText("Edit your credit card number here: ");
        }
        Button btn=new Button("Check");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                long number=Long.parseLong(textField.getText());
                label2.setText("Validity: "+ (isValid(number) ? "Valid": "Invalid"));
            }
        });
        FlowPane flowPane3=new FlowPane(10,10);
        flowPane3.setPadding(new Insets(10));
        flowPane3.setOrientation(Orientation.VERTICAL);
        flowPane3.setAlignment(Pos.CENTER);
        flowPane3.getChildren().addAll(label3,label1,textField,btn,label2);
        flowPane3.setStyle("-fx-background-color: #D8BFD8;");
        return flowPane3;
    }


    public static boolean isValid(long number)
    {
        return (getSize(number) >= 13 &&
                getSize(number) <= 16) &&
                (prefixMatched(number, 4) ||
                        prefixMatched(number, 5) ||
                        prefixMatched(number, 37) ||
                        prefixMatched(number, 6)) &&
                ((sumOfDoubleEvenPlace(number) +
                        sumOfOddPlace(number)) % 10 == 0);
    }


    public static int sumOfDoubleEvenPlace(long number)
    {
        int sum = 0;
        String num = number + "";
        for (int i = getSize(number) - 2; i >= 0; i -= 2)
            sum += getDigit(Integer.parseInt(num.charAt(i) + "") * 2);

        return sum;
    }


    public static int getDigit(int number)
    {
        if (number < 9)
            return number;
        return number / 10 + number % 10;
    }


    public static int sumOfOddPlace(long number)
    {
        int sum = 0;
        String num = number + "";
        for (int i = getSize(number) - 1; i >= 0; i -= 2)
            sum += Integer.parseInt(num.charAt(i) + "");
        return sum;
    }


    public static boolean prefixMatched(long number, int d)
    {
        return getPrefix(number, getSize(d)) == d;
    }


    public static int getSize(long d)
    {
        String num = d + "";
        return num.length();
    }


    public static long getPrefix(long number, int k)
    {
        if (getSize(number) > k) {
            String num = number + "";
            return Long.parseLong(num.substring(0, k));
        }
        return number;
    }
}
