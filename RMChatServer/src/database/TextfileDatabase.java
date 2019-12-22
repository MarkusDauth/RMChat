package database;

public class TextfileDatabase implements DatabaseInterface{
    @Override
    public synchronized boolean checkPassword(String username, String password) {
        return false;
    }

    @Override
    public synchronized void registerUser(String username, String password) {

    }
}
