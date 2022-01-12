package dev.mantas.vikop2app.ui.common;

import javafx.scene.control.Button;

import java.util.function.Supplier;

public interface TableActionButtonProvider<T> {

    Button createButton(int index, Supplier<T> dataProvider, Supplier<Button[]> activeButtonsContext);

}
