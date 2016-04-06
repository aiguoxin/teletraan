package json;

import java.util.Calendar;

/**
 * 16/4/5 下午6:18
 * aiguoxin
 * 说明:
 */
public class TimeTest {

    public static void main(String[] args){
        long time = System.currentTimeMillis();
        System.out.println(time/1000);

        System.out.println(getTimesmorning());
    }

    public static int getTimesmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis()/1000);
    }

}
