import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Tavolo {

    /** attribute **/
    public static final int N_R = 3;
    public static final int N_C = 3;
    private int[][] table;

    /** constructor **/
    public Tavolo() {
        this.table = new int[N_R][N_C];
        for(int i = 0; i < N_R; i++) {
            for(int j = 0; j < N_C; j++){
                this.table[i][j] = 1;
            }
        }
    }

    /**
     * Set point method
     * **/
    public void setPoint(int r, int c, int point){
        try{
            if(point == 3 || point == 5 || point == 1){
                this.table[r][c] = point;
            }else{
                throw new InvalidParameterException();
            }
        }catch(InvalidParameterException e){}
    }


}

}