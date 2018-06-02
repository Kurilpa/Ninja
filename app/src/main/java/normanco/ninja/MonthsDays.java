package normanco.ninja;

import java.util.ArrayList;
import java.util.Map;

public class MonthsDays {
    public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static final int JAN_DAYS = 31,
            FEB_DAYS = 28, FEB_DAYS_LEAP = 29,
            MAR_DAYS = 31,
            APR_DAYS = 30,
            MAY_DAYS = 31,
            JUN_DAYS = 30,
            JUL_DAYS = 31,
            AUG_DAYS = 31,
            SEP_DAYS = 30,
            OCT_DAYS = 31,
            NOV_DAYS = 30,
            DEC_DAYS = 31;

    public static final int[] DAYS_YEAR = {JAN_DAYS,
            FEB_DAYS,
            MAR_DAYS,
            APR_DAYS,
            MAY_DAYS,
            JUN_DAYS,
            JUL_DAYS,
            AUG_DAYS,
            SEP_DAYS,
            OCT_DAYS,
            NOV_DAYS,
            DEC_DAYS};

    public static final int[] DAYS_YEAR_LEAP = {JAN_DAYS,
            FEB_DAYS_LEAP,
            MAR_DAYS,
            APR_DAYS,
            MAY_DAYS,
            JUN_DAYS,
            JUL_DAYS,
            AUG_DAYS,
            SEP_DAYS,
            OCT_DAYS,
            NOV_DAYS,
            DEC_DAYS};

    public static final int[] LEAP_YEARS = {2012,
            2016,
            2020,
            2024,
            2028,
            2032,
            2036};
}
