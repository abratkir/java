package gauss;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Robert Abratkiewicz
 */

public class SparseMatrix {
    private int line = 0;
    private int column = 0;
    private Tree [] lineTable;
    private int [] lineChange;
    private double [] coefficient;
    
    //Tworzenie macierzy na podstawie danych z pliku
    public SparseMatrix(String pathToMatrix) {
        read(pathToMatrix);
    }
    
    //Tworzenie wektora x, który jest rozwiązaniem równania A*x=B
    public SparseMatrix (SparseMatrix A, SparseMatrix B) throws Zla_macierz {
        if ((B.getColumn() == 1) && (B.getLine() == A.getLine()) && (B.getLine() == A.getColumn())) {
            line = B.getLine();
            column = B.getColumn();
            lineTable = new Tree[line];
            for (int i = 0; i < line; i++) lineTable[i] = new Tree();
            lineChange = new int[line];
            for (int i = 0; i < line; i++) lineChange[i] = i;
            coefficient = new double[line];
            for (int i = 0; i < line; i++) coefficient[i] = 0;
            elimination(A, B);
            wynik(A, B);
            try {
                przywracanie();
            } catch (Element_poza_zakresem e) {
                System.err.println("Błąd przywracania początkowej kolejności wektora niewiadomych");
            }
            try {
                write();
            } catch (IOException ex) {
                System.err.println("Nieudany zapis wektora wynikowego do pliku");
            }
        } else System.err.println("Wymiary macierzy nie są poprawne");
    }
    
    //funkcja odpowiadająca za przywrócenie wartości wektora niewiadomych do kolejności początkowej
    private void przywracanie () throws Element_poza_zakresem {
        for (int i = 0; i < line; i++) {
            while (i != lineChange[i]) {
                int temp = lineChange[i];
                lineChange[i] = lineChange[temp];
                lineChange[temp] = temp;
                double temp2 = getValue(temp, 0);
                insert(temp, 0, getValue(i, 0));
                insert(i, 0, temp2);
            }
        }
        
    }
    
    //funkcja wyliczająca z macierzy trójkątnej i macierzy B wartości niewiadomych
    private void wynik (SparseMatrix A, SparseMatrix B) {
        for (int i = line - 1; i >= 0; i--) {
            try {
                insert(i, 0, wynik(A, B, i));
            } catch (Element_poza_zakresem ex) {
                System.err.println("Błąd wyliczania niewiadomych");
            }
        }
    }
    private double wynik (SparseMatrix A, SparseMatrix B, int line) throws Element_poza_zakresem {
        double wynik = B.getValue(line, 0);
        for (int i = this.line - 1; i > line; i--) {
            wynik -= A.getValue(line, i)*getValue(i, 0);
        }
        wynik = wynik/A.getValue(line, line);
        return wynik; 
    }
    
    //funkcja która odpowiada za uzyskanie macierzy trójkątnej
    private void elimination (SparseMatrix A, SparseMatrix B) throws Zla_macierz {
        for (int i = 0; i < line - 1; i++) {
            try {
                if ((A.getRoot(i) == null) || (A.getValue(i, i) == 0)) {
                    int help = 0;
                    //petla odpowiadająca za zamianę wierszy jeśli na przekątnej jest element o wartości 0 
                    //lub nie ma elementu co jest jednoznaczne z tym, że jego wartość wynosi 0
                    for (int j = line - 1; j > i && help == 0; j--) {
                        try {
                            if ((A.getRoot(j) != null) && (A.getValue(j, i) != 0)) {
                                help++;
                                Node temp = A.getRoot(i);
                                A.setRoot(i, A.getRoot(j));
                                A.setRoot(j, temp);
                                temp = B.getRoot(i);
                                B.setRoot(i, B.getRoot(j));
                                B.setRoot(j, temp);
                                int temp2 = lineChange[i];
                                lineChange[i] = lineChange[j];
                                lineChange[j] = temp2;
                            }
                        } catch (Element_poza_zakresem ex) {
                            System.err.println("błąd");
                        }
                    }
                    //jeśli nie udało się znaleźć wiersza (o większym numerze niż ten dla którego szukaliśmy), 
                    //który ma niezerowy element w tej kolumnie to jest to jednoznaczne z tym, że nie da
                    //się rozwiązać tego równania
                    if (help == 0) throw new Zla_macierz("Na podstawie wprowadzonej macierzy nie da się obliczyć wszystkich niewiadomych");
                }
                if ((A.getRoot(i) != null) && (A.getValue(i, i) != 0)) {
                    for (int j = i+1; j < line; j++) coefficient[j] = A.getValue(j, i)/A.getValue(i, i);
                    double valueB = B.getValue(i, 0);
                    double help;
                    //pętla odpowiadająca za wyliczenie współczynników dla każdego wiersza
                    //współczynniki te mówią nam jaki stosunek wartości wiersza odejmujemy od wierszy znajdujących się niżej
                    for (int j = i + 1; j < line; j++) {
                        help = valueB*coefficient[j];
                        if (help != 0) B.addValue(j, help, 0);
                    }
                    //funkcja która odejmuje od wszystkich wierszy niżej wartość wiersza dla którego obecnie 
                    //przeprowadzamy działanie z uwzględnieniem współczynników dla każdego wiersza
                    elimination(A, A.getRoot(i), i);
                }
                    
            } catch (Element_poza_zakresem ex) {
                System.err.println("błąd");
            }
        }
    }
    
    private void elimination (SparseMatrix A, Node lineA, int line) {
        if (lineA.getLeft() != null) elimination(A, lineA.getLeft(), line);
        double valueA = lineA.getValue();
        int columnNumber = lineA.getColumn();
        double help;
        for (int i = line + 1; i < this.line; i++) {
            help = valueA*coefficient[i];
            //pozwala na unikanie dopisywania do macierzy niepotrzebnych elementów zerowych
            if (help != 0) A.addValue(i, help, columnNumber);
        }
        if (lineA.getRight() != null) elimination(A, lineA.getRight(), line);    
    }
    
    //funkcja pobierająca wartość z macierzy
    public double getValue (int line, int column) throws Element_poza_zakresem {
        if ((this.line > line) && (line >= 0) && (column >= 0) && (this.column > column)) return lineTable[line].getValue(column);
        else throw new Element_poza_zakresem("Element poza zakresem tablicy");
    }
    
    //zmienna help pozwala dodrukować 0, które normalnie nie są przechowywane w macierzach
    public void print () {
        int help;
        for  (int i = 0; i < line; i++) {
            if (lineTable[i].getRoot() != null) help = print(lineTable[i].getRoot(), 0);
            else help = 0;
            while (help < column) {
                System.out.print(" 0 \t");
                help++;
            }
            System.out.println();
        }
    }
    //zmienna printHelp dodrukowuje 0 w miejscach w których nie ma zapisanych elementów w macierzy
    private int print (Node root, int printHelp) {
        if (root.getLeft() != null) printHelp = print(root.getLeft(), printHelp);
        while (printHelp < root.getColumn()) {
            System.out.print(" 0 \t");
            printHelp++;
        }
        printHelp++;
        System.out.print(root.getValue() + "\t");
        if (root.getRight() != null) printHelp = print(root.getRight(), printHelp);
        return printHelp;
    }
   
    public Node getRoot (int line) {
        return lineTable[line].getRoot();
    }
    
    public int getLine () {
        return line;
    }
    public int getColumn () {
        return column;
    }
    
    //Wczytywanie macierzy z pliku
    private void read (String pathToMatrix) {
        File source = new File(pathToMatrix);
        
        try {
            Scanner in = new Scanner(source);
            
            try {
                line = in.nextInt();
                column = in.nextInt();
                lineTable = new Tree[line];
                for (int i = 0; i < line; i++) lineTable[i] = new Tree();
                int helpLine;
                int helpColumn;
                double helpValue;
                while (in.hasNext()) {
                    helpLine = in.nextInt();
                    helpColumn = in.nextInt();
                    helpValue = in.nextDouble();
                    try {
                        insert(helpLine, helpColumn, helpValue);
                    } catch (Element_poza_zakresem e) {
                        System.err.println(e);
                    }    
                }
            } catch (Exception e) {
                System.err.println("Źle sformatowany plik z danymi");
            }
            
            in.close();
        } catch (FileNotFoundException e) {
            System.err.println("Zła ścieżka do pliku");
        }
    }
    
    //dodaje nowy węzeł lub w przypadku istnienia węzła zastępuje jego wartość
    private void insert (int line, int column, double value) throws Element_poza_zakresem {
        if ((this.line > line) && (line >= 0) && (this.column > column) && (column >= 0)) {
            lineTable[line].addNode(column, value);
        } else throw new Element_poza_zakresem("Element poza zakresem podanych rozmiarów macierzy");
        
    }
    
    
    //funkcja zapisująca macierz do pliku o nazwie MatrixX.txt
    public void write () throws IOException {
        FileWriter out = new FileWriter("MatrixX.txt");
        out.write(line + " " + column + "\r\n");
        for  (int i = 0; i < line; i++) {
            if (lineTable[i].getRoot() != null) write(lineTable[i].getRoot(), i, out);
        }
        out.close();
    }
    
    private void write (Node root, int line, FileWriter out) throws IOException {
        if (root.getLeft() != null) write(root.getLeft(), line, out);
        //znak nowej linii w notatniku to \r\n, zazwyczaj w innych programach to \n
        //w systemie linux to \n, a w systemach Apple \r
        out.write(line + " " + root.getColumn() + " " + root.getValue() + "\r\n");
        if (root.getRight() != null) write(root.getRight(), line, out);
    }
    
    //funkcja pomagająca w zmianie kolejności wierszy w macierzy
    public void setRoot(int line, Node newRoot) {
        if (lineTable[line] != null) {
            lineTable[line].setRoot(newRoot);
        }
    }
    
    //funkcja odpowiadająca za dodanie do obecnej wartości -value (pomaga w wyliczaniu z macierzy trójkątnej rozwiązania liniowego)
    public void addValue(int line, double value, int column) {
        lineTable[line].addValue(value, column);
    }
}