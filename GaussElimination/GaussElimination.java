package gauss;
/**
 * @author Robert Abratkiewicz
 */
public class GaussElimination {
    private SparseMatrix matrixA = null;
    private SparseMatrix matrixB = null;
    private SparseMatrix matrixX = null;
    
    public GaussElimination (String pathToMatrixA, String pathToMatrixB) throws Zla_macierz {
        matrixA = new SparseMatrix(pathToMatrixA);
        matrixB = new SparseMatrix(pathToMatrixB);
        System.out.println("\n Macierz A \n");
        print(1);
        System.out.println("\n Macierz B \n");
        print(2);
        matrixX = new SparseMatrix(matrixA, matrixB);
    }
    
    //wyświetlenie na ekranie macierzy rzadkiej
    public void print (int matrixNumber) {
        switch (matrixNumber) {
            case 1: 
                matrixA.print();
                break;
            case 2:
                matrixB.print();
                break;
            case 3:
                matrixX.print();
                break;
            default:
                System.out.println("Źle podany numer macierzy");
        }
    }
    
    //pobranie wartości z macierzy
    public double getValue (int matrixNumber, int line, int column) {
        
        try {
            switch(matrixNumber) {
                case 1:
                    return matrixA.getValue(line, column);
                case 2:
                    return matrixB.getValue(line, column);
                case 3:
                    return matrixX.getValue(line, column);
                default:
                    return 0;
            }
        } catch (Element_poza_zakresem e) {
            System.err.println(e);
            return 0;
        }
    }
}