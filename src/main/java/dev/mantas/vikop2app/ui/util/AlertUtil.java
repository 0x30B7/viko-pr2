package dev.mantas.vikop2app.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {

    public static void alert(Alert.AlertType type, String... message) {
        new Alert(type, String.join("\n", message), ButtonType.OK).showAndWait();
    }

    public static boolean alertYesNo(String... message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, String.join("\n", message), ButtonType.YES, ButtonType.CANCEL);
        Optional<ButtonType> clicked = alert.showAndWait();
        return clicked.isPresent() && clicked.get() == ButtonType.YES;
    }

}
