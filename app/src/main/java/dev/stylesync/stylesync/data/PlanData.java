package dev.stylesync.stylesync.data;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.stylesync.stylesync.R;
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

    public static List<ViewPagerItem> convertPlanDataToViewPagerItems(PlanData planData) {
        String[][] planDataItems = { planData.getPlan1(), planData.getPlan2(), planData.getPlan3() };
        return IntStream.range(0, planDataItems.length)
                .mapToObj(i ->
                        new ViewPagerItem(R.drawable.baseline_10k_24,
                                "Plan " + (i + 1),
                                Arrays.toString(planDataItems[i])))
                .collect(Collectors.toList());
    }
}
