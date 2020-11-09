package planningalgorithm;

import javafx.concurrent.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class TestTask extends Task<List<Integer>> {


    @Override 
    protected List<Integer> call() throws Exception{
        List<Integer> Gen = new ArrayList<>();
        for (int i=1;i<10;i++){
            Gen.add(Integer.valueOf(i));
            this.updateProgress(i, 10);
            TimeUnit.SECONDS.sleep(10);
        }
        return Gen;
    }

}
