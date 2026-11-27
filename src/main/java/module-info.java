module ca.qc.bdeb.sim.gontrandlefletan {
    requires javafx.controls;
    requires javafx.fxml;


    opens ca.qc.bdeb.sim.gontrandlefletan to javafx.fxml;
    exports ca.qc.bdeb.sim.gontrandlefletan;
}