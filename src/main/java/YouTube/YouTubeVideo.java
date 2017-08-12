package YouTube;

public class YouTubeVideo {
    String kind;
    String etag;
    Id id;
    Snippet snippet = new Snippet();
}

class Id {
    String kind;
    String videoId;
}
class Snippet {
    String publishedAt;
    String channelId;
    String title;
    String description;
    Thumbnails thumbnails;
    String channelTitle;
    String liveBroadcastContent = "none";
}

class Thumbnail {
    String url;
    int width;
    int height;
}
class Thumbnails {
    //Thumbnail default;
    Thumbnail medium;
    Thumbnail high;
}