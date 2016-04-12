package json;

import com.pinterest.deployservice.common.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * 16/4/5 下午6:18
 * aiguoxin
 * 说明:
 */
public class TimeTest {

    public static void main(String[] args){
        String dateStr = "05/04/2016";
        System.out.println(StringUtils.isBlank(CommonUtils.changeDateFromat(dateStr)));
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
