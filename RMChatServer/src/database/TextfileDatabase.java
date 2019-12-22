package database;

public class TextfileDatabase implements DatabaseInterface {
    private static TextfileDatabase instance = new TextfileDatabase();

    private TextfileDatabase(){};

    public static DatabaseInterface getInstance(){
        return instance;
    }

    @Override
    public synchronized boolean checkPassword(String username, String password) {
        return false;
    }

    @Override
    public synchronized void registerUser(String username, String password) {

    }
}
