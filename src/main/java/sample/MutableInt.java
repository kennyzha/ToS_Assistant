package sample;

public class MutableInt {
    private int value;

    public MutableInt(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void incrementValue(){
        value++;
    }
}
