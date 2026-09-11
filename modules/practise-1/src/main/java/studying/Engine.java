package studying;

import lombok.Getter;

public class Engine {
    @Getter
    private final int size;
    public Engine(int size){
        this.size = size;
    }
    @Override
    public String toString(){
        return "Engine{" +
                "size=" + size +
                '}';
    }
}
