package normanco.ninja;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import normanco.ninja.NumToWords;


public class Baby  {
    private static String TAG = "LogMessage";
    private static final int FULL_TERM = 280;

    private int dateBorn;
    private int dateDue;

    public Baby(int dateDue, int dateBorn){
        this.dateBorn = dateBorn + 100;
        this.dateDue = dateDue + 100;
    }

    public int getAgeDays(long age){
        return (int) age % 7;
    }

    public int getAgeWeeks(long age){
        return (int) age / 7;
    }

    public int getMonths(long age){
        return (int) ((double) age / 30.4166) % 12;
    }

    public int getYears(long age){
        return (int) age / 365;
    }

    public String getGestation(){
        long gestation;
        String weeks, days, result;
        gestation = FULL_TERM + getAgeCorrected() - getAgeActual();
        weeks = NumToWords.convert(gestation / 7);
        days = NumToWords.convert(gestation % 7);
        result = weeks + " weeks & " + days + " days";

        return result;
    }

    public long getAgeActual(){
        long ageActual;
        int currentDate;
        currentDate = getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        try{
            Date simpleDateBorn = simpleDateFormat.parse(Integer.toString(dateBorn));
            Date simpleDateNow = simpleDateFormat.parse(Integer.toString(currentDate));
            long diffInMillies = simpleDateNow.getTime() - simpleDateBorn.getTime();
            ageActual = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        catch(ParseException e){
            return 999;
        }
        return ageActual;
    }

    public long getAgeCorrected(){
        long ageCorrected;
        int currentDate;
        currentDate = getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        try{
            Date simpleDateDue = simpleDateFormat.parse(Integer.toString(dateDue));
            Date simpleDateNow = simpleDateFormat.parse(Integer.toString(currentDate));
            long diffInMillies = simpleDateNow.getTime() - simpleDateDue.getTime();
            ageCorrected = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        catch(ParseException e){
            return 9999;
        }
        return ageCorrected;
    }

    private int getCurrentDate(){
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        return year*10000 + month*100 + day;
    }
}
