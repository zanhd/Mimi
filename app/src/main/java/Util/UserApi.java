package Util;

public class UserApi {

    //this class diffentiate btw two Users

    private String userId;
    private static UserApi userApi;

    private String username;
    private String name;
    private String email;

    //to make it singleton
    public static UserApi getInstance() {
        if(userApi != null) return userApi;
        userApi = new UserApi();
        return userApi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static UserApi getUserApi() {
        return userApi;
    }

    public static void setUserApi(UserApi userApi) {
        UserApi.userApi = userApi;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
