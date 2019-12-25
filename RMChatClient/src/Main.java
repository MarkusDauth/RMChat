import controller.NetworkController;
import view.Views;

public class Main {


    public static void main(String[] args) {

        //MVC setup
        NetworkController networkController = new NetworkController();
        //Launch
        Views.launchApplication(networkController);
    }



}
