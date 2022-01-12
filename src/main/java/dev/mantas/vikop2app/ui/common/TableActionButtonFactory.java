package dev.mantas.vikop2app.ui.common;

import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TableActionButtonFactory {

    public static <UI_TYPE, DATA_TYPE, CONTROLLER_TYPE> TableActionButtonProvider<UI_TYPE> createModifyButton(
            Class<UI_TYPE> uiType, Class<DATA_TYPE> dataType, Class<CONTROLLER_TYPE> controllerType,
            Function<UI_TYPE, DATA_TYPE> dataSource, ObservableList<UI_TYPE> validEntryList,
            String controllerResource, BiConsumer<CONTROLLER_TYPE, DATA_TYPE> controllerModifier) {
        return (index, dataProvider, btnContext) -> {
            Button btn = new Button("Modifikuoti");
            btn.setOnAction((e) -> {
                UI_TYPE data = dataProvider.get();
                Button[] buttons = btnContext.get();

                for (Button button : buttons) {
                    button.setDisable(true);
                }

                AsyncTask.async(() -> dataSource.apply(data)).then((validResult) -> {
                    if (validResult == null) {
                        validEntryList.removeAll(data);
                    } else {
                        SceneUtil.load(e, controllerResource, controllerType, (controller) -> {
                            controllerModifier.accept(controller, validResult);
                        });
                    }
                }).lastly(() -> {
                    for (Button button : buttons) {
                        button.setDisable(false);
                    }
                }).execute();
            });
            return btn;
        };
    }

    public static <UI_TYPE, DATA_TYPE> TableActionButtonProvider<UI_TYPE> createRemoveButton(
            Class<UI_TYPE> uiType, Class<DATA_TYPE> dataType,
            Function<UI_TYPE, Boolean> dataSource, ObservableList<UI_TYPE> validEntryList,
            String... confirmMessage) {
        return (index, dataProvider, btnContext) -> {
            Button btn = new Button("Šalinti");
            btn.setOnAction((e) -> {
                UI_TYPE data = dataProvider.get();
                Button[] buttons = btnContext.get();

                for (Button button : buttons) {
                    button.setDisable(true);
                }

                if (confirmMessage != null && confirmMessage.length > 0) {
                    if (!AlertUtil.alertYesNo(confirmMessage)) {
                        for (Button button : buttons) {
                            button.setDisable(false);
                        }
                        return;
                    }
                }

                AsyncTask.async(() -> dataSource.apply(data)).then((success) -> {
                    if (success) {
                        validEntryList.removeAll(data);
                    }
                }).lastly(() -> {
                    for (Button button : buttons) {
                        button.setDisable(false);
                    }
                }).execute();
            });
            return btn;
        };
    }

}
