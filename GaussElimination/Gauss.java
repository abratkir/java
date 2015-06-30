package gauss;
/**
 * @author Robert Abratkiewicz
 */
public class Gauss {

    public static void main(String[] args) {
		
        try {
            GaussElimination matrix = new GaussElimination(args[0], args[1]);
            
            System.out.println("\n Macierz trójkątna A \n");
            matrix.print(1);
            System.out.println("\n Macierz B po przekształceniu macierzy A do trójkątnej \n");
            matrix.print(2);
            System.out.println("\n Macierz X - wektor niewiadomych \n");
            matrix.print(3);
        } catch (Zla_macierz ex) {
            System.err.println("\n Na podstawie wprowadzonej macierzy nie da się obliczyć wszystkich niewiadomych\n");
        }
        
    }
    
}
