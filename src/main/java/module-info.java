module com.github.alexandrelupascu.javafx3drenderer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.alexandrelupascu.javafx3drenderer to javafx.fxml;
    exports com.github.alexandrelupascu.javafx3drenderer;
}