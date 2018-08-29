
package login.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import dashboard.view.DashboardController;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import login.User;

public class LoginController implements Initializable {
    
    @FXML
    private TextField userCode;
    @FXML
    private PasswordField password;
    @FXML
    private RadioButton consultant;
    @FXML
    private RadioButton doctor;
    @FXML
    private ToggleGroup userType;
    @FXML
    private Label errorLabel;


    @FXML
    private void login(ActionEvent event) throws IllegalAccessException, IOException, InstantiationException, SQLException, NoSuchAlgorithmException, ClassNotFoundException {
        String INCOMPLETE = "Preencha todos os campos.";
        String INVALID = "Login inválido.";

        errorLabel.setText("");

        //Form validations
        String code = userCode.getText(),
                pass = password.getText();
        char loginType;
        Toggle type = userType.getSelectedToggle();
        if (code.equals("") || pass.equals("")) {
            errorLabel.setText(INCOMPLETE);
            return;
        }
        if (type == consultant) {
            loginType = 'C';
        } else if (type == doctor ){
            loginType = 'D';
        } else  {
            errorLabel.setText(INCOMPLETE);
            return;
        }

        User user = new User(code, pass, loginType);
        if (user.login()) {
            /* Goes to dashboard passing the user id */
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../dashboard/view/Dashboard.fxml"));
            DashboardController controller = new DashboardController(2);
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Node node=(Node) event.getSource();
            Stage stage=(Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            errorLabel.setText(INVALID);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
