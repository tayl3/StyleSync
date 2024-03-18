package dev.stylesync.stylesync.data;

import java.util.Arrays;

public class PlanData implements Data {
    public String[] plan1;
    public String[] plan2;
    public String[] plan3;

    @Override
    public String toString() {
        return "PlanData{" +
                "plan1=" + Arrays.toString(plan1) +
                ", plan2=" + Arrays.toString(plan2) +
                ", plan3=" + Arrays.toString(plan3) +
                '}';
    }
}
