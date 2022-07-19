package hongwei.javaSE.method;

import java.util.Arrays;

public class Sort {
    public static void main(String[] args) {
        int[] a = {1,56,12,678,234,5};
        Sort.sort(a);
        System.out.println(Arrays.toString(a));
    }
    public static int[] sort(int[] array){
        int temp;
        for (int i = 0; i < array.length-1; i++) {
            for (int j = 0; j < array.length-1-i; j++) {
                if(array[j]>array[j+1]){
                    temp = array[j+1];
                    array[j+1]=array[j];
                    array[j]=temp;
                }
            }
        }
        return array;
    }
   public void b(){
       int[] a = {1,56,12,678,234,5};
        sort(a);
   }

}
