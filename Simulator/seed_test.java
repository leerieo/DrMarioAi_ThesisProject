public class seed_test {

    private static StringBuilder sb = new StringBuilder();
    public static void main(String [] args){
        int seed = 256*0x00 + 0x02; 

        int l = 0; 

        level.process_level(seed, l, true, true,"ALS",2,sb);
 
    }
}
