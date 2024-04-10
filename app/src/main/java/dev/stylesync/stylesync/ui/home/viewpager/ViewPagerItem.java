package dev.stylesync.stylesync.ui.home.viewpager;

public class ViewPagerItem {

    private int imageId;
    private String heading;
    private String description;

    public ViewPagerItem(int imageId, String heading, String description) {
        this.imageId = imageId;
        this.heading = heading;
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
