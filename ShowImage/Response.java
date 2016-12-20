package ShowImage;
/**
 *
 *
 *
 */
class Response {
    App photos;
}

class App {
    String total;
    int page;
    int pages;
    int perpage;
    PhotoInfo [] photo;

}

class PhotoInfo {
    int isfamily;
    int ispublic;
    int isfriend;
    String id;
    int farm;
    String title;
    int context;
    String owner;
    String secret;
    String server;
    float longitude;
    float latitude;
    int accuracy;
}

    