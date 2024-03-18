package dev.stylesync.stylesync.data;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "PlanData{" +
                "plan1=" + Arrays.toString(plan1) +
                ", plan2=" + Arrays.toString(plan2) +
                ", plan3=" + Arrays.toString(plan3) +
                '}';
    }
}
