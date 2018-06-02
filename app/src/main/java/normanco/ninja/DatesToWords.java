package normanco.ninja;

/**
 * Created by toman on 4/05/2018.
 */

import android.widget.Switch;

import normanco.ninja.MonthsDays;

public class DatesToWords {

    private final static String[] MONTHS = MonthsDays.MONTHS;
    private static int year;
    private static int month;
    private static int dayOfMonth;

    private DatesToWords(){

    }

    public static String parseDateToWord(int date){
        setValues(date);
        return Integer.toString(dayOfMonth) + " " + monthToWord() + " " + Integer.toString(year);
    }

    private static void setValues(int date){
        year = date / 10000;
        month = (date % 10000) / 100;
        dayOfMonth = date % 100;
    }

    private static String monthToWord(){
        String word;
        switch (month){
            case 0: word = MONTHS[month];
                    break;
            case 1: word = MONTHS[month];
                break;
            case 2: word = MONTHS[month];
                break;
            case 3: word = MONTHS[month];
                break;
            case 4: word = MONTHS[month];
                break;
            case 5: word = MONTHS[month];
                break;
            case 6: word = MONTHS[month];
                break;
            case 7: word = MONTHS[month];
                break;
            case 8: word = MONTHS[month];
                break;
            case 9: word = MONTHS[month];
                break;
            case 10: word = MONTHS[month];
                break;
            case 11: word = MONTHS[month];
                break;
            default: word = "Month";
        }
        return word;
    }
}
