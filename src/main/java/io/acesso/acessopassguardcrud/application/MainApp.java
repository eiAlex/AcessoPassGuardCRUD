package io.acesso.acessopassguardcrud.application;



import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application class, onde a mágia começa em JavaFX
 */
public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/Layout.fxml"));
  
         
             
		
// Monta a cena
		Scene scene = new Scene(root, 850, 400);
		
                //Css para a Cena
                scene.getStylesheets().add("/styles/Styles.css");
                
                
		// Define parâmetros para a janela
		primaryStage.setMinWidth(850);
		primaryStage.setMinHeight(400);
		primaryStage.setMaxHeight(800);
		primaryStage.setTitle("Acesso Pass Guard CRUD");
		
		// Atrela a cena é janela
		primaryStage.setScene(scene);
		
		// Exibe a janela
		primaryStage.show();
	}

	public static void main(String[] args) throws Exception {
		// Inicializa o JavaFX
		launch(args);
	}
}
