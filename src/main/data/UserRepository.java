package main.data;

public class UserRepository {
    private static UserRepository instance = null;

    private UserRepository() { }

    public static UserRepository getInstance() {
        if(instance == null) {
            synchronized (UserRepository.class) {
                if(instance == null) {
                    instance = new UserRepository();
                }
            }
        }

        return instance;
    }
}
