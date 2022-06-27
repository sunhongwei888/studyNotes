package hongwei;

import java.util.Calendar;

public class test {
    public static void main(String[] args) {
        Calendar c1 = Calendar.getInstance();
        c1.set(2017, 2, 1);
        System.out.println(c1.get(Calendar.YEAR)
                +"-"+c1.get(Calendar.MONTH)
                +"-"+c1.get(Calendar.DATE));
        c1.set(2017, 2, 0);
        System.out.println(c1.get(Calendar.YEAR)
                +"-"+c1.get(Calendar.MONTH)
                +"-"+c1.get(Calendar.DATE));
    }


}
