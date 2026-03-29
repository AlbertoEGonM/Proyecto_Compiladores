import GUI.Panel;

public class Main{
    public static void main(String[] args) {
        for(int i = 32; i<127; i++)
            System.out.println((char)i + "="+ i + ",");
        for(int i = 161; i<256; i++)
            System.out.println((char)i + "="+ i + ",");
        new Panel().setVisible(true);
        
    }
}
