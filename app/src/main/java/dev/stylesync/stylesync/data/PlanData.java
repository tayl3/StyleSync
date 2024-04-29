package dev.stylesync.stylesync.data;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.service.UserService;
import dev.stylesync.stylesync.ui.home.viewpager.ViewPagerItem;

public class PlanData implements Data {
    private String[] plan1;
    private String[] plan2;
    private String[] plan3;

    public String[] getPlan1() {
        return plan1;
    }

    public String[] getPlan2() {
        return plan2;
    }

    public String[] getPlan3() {
        return plan3;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlanData{" +
                "plan1=" + Arrays.toString(plan1) +
                ", plan2=" + Arrays.toString(plan2) +
                ", plan3=" + Arrays.toString(plan3) +
                '}';
    }

    public static List<ViewPagerItem> convertPlanDataToViewPagerItems(PlanData planData, List<UserData.Cloth> clothes) {
        String[][] planDataItems = { planData.getPlan1(), planData.getPlan2(), planData.getPlan3() };
        return IntStream.range(0, planDataItems.length)
                .filter(i -> planDataItems[i] != null && planDataItems[i].length > 0)
                .mapToObj(i ->
                        new ViewPagerItem(getUrlsForPlannedClothes(planDataItems[i], clothes),
                                "Plan " + (i + 1),
                                Arrays.toString(planDataItems[i])))
                .collect(Collectors.toList());
    }

    private static List<String> getUrlsForPlannedClothes(String[] plannedClothes, List<UserData.Cloth> userClothes) {
        List<String> urls = Arrays.stream(plannedClothes)
                .map(plannedCloth -> userClothes.stream()
                        .filter(cloth -> cloth.getDescription().equalsIgnoreCase(plannedCloth))
                        .findFirst()
                        .map(UserData.Cloth::getUrl)
                        .orElse(null))
                .collect(Collectors.toList());

        return urls;
    }
}
