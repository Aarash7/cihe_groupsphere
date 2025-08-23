module com.mycompany.cihe_groupsphere {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.mycompany.cihe_groupsphere to javafx.fxml;
    exports com.mycompany.cihe_groupsphere;
}
