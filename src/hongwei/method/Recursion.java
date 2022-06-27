package hongwei.method;

public class Recursion {
    public static void main(String[] args) {
        Recursion recursion = new Recursion();
        long a = recursion.test(5l);
        System.out.println(a);

    }

    public long test(long n){
        if(n==1l){
            return 1l;
        }else{
            System.out.println(n);
            return n*test(n-1);
        }
    }
}
