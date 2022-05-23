import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class test {

    private static StringBuilder sb = new StringBuilder();

    public static void main (String[] args){

        
        // 2 parameters category + version
        String category = "TVO";
        int ver; 
        String csv_name;
        if(args.length>=2){
            category = args[0]; 
            ver = Integer.parseInt(args[1]); 
            csv_name = args[0] +"_v"+ args[1] +"_test.csv"; 
        }else{
            return; 
        }
     
        sb.append("seed"); 
        sb.append(','); 
        sb.append("level"); 
        sb.append(','); 
        sb.append("vitamins_used"); 
        sb.append(','); 
        sb.append("finished"); 
        sb.append(','); 
        sb.append("num_ver_clears"); 
        sb.append(','); 
        sb.append("num_hor_clears"); 
        sb.append(','); 
        sb.append("num_vita_placed_top_row"); 
        sb.append('\n');

        // int counter = 0; 

        // // // // REMEMBER TO CHANGE THESE TO 256!!!
        // for(int i=0; i<256; i++){
        //     System.out.println("i: "+i);
        //     for(int j=0; j<256; j++){
        //         if(j%20==0){
        //             System.out.println("    j: "+j);
        //         }
        //         //Skip over seeds 00 00, 00 01, 01 00, and 01 01.
        //         if(i<=1 && j<=1){
        //             continue; 
        //         }else{ 
        //             int seed = 256*i + j; 
        //             for(int l=0; l<=20; l++){
        //                 level.process_level(seed, l, false, true,category,ver,sb);
        //                 counter++; 
        //             }
        //         }

        //     }
        // }
        // System.out.println("END!: "+counter);

        // try (PrintWriter writer = new PrintWriter(csv_name)) {
        //     writer.write(sb.toString());
        //     System.out.println("Written!");
        // }catch (FileNotFoundException e) {
        //     System.out.println(e.getMessage());
        // }

        // System.out.println("WRITTEN!");


        int seed = 256*0x99 + 0x99; 
        int l = 15; 
        level.process_level(seed, l, true, false,category,ver,sb);

        // int seed = 256*0x99 + 0x99; 

        // int l = 20; 

        // level.process_level(seed, l,true,false);
        // System.out.println("END!");
        
    }
}
