package dev.stylesync.stylesync.ui.home.viewpager;

import java.util.List;

public class ViewPagerItem {

    private List<String> imageUrls;
    private String heading;
    private String description;

    public ViewPagerItem(List<String> imageUrls, String heading, String description) {
        this.imageUrls = imageUrls;
        this.heading = heading;
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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
