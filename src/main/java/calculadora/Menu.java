package calculadora;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

public class Menu extends JFrame{
    private JPanel mainPanel;
    private JButton btnTLineales;
    private JButton btnPinterior;
    private JButton btnDQr;
    private JTextField vectorSize;
    private JTextField matrizSize;
    private JButton btnGenerate;
    private JPanel panelMatriz;
    private JPanel panelVector;
    private JButton btnSaveV;
    private JLabel MTitulo;
    private JLabel VTitulo;
    private JButton btnSaveM;
    private JPanel panelVector2;
    private JButton btnOperacion;
    private JTextArea textResultado;
    private String op="";

    public Menu(){
        initComponents();
        matrizSize.setEnabled(false);
        vectorSize.setEnabled(false);
        btnTLineales.addActionListener(e->{
            matrizSize.setEnabled(true);
            vectorSize.setEnabled(true);
            op="tLineal";
        });
        btnPinterior.addActionListener(e->{
            vectorSize.setEnabled(true);
            op="pInterior";
        });

        btnGenerate.addActionListener(a -> {
            btnOperacion.setVisible(true);

            switch (op) {
                case "tLineal":
                    var matrizCampos = generarMatriz();
                    var vectorCampos = generarVector(panelVector);
                    btnOperacion.addActionListener(o -> {
                        var resultado = linealTransform(saveMatriz(matrizCampos), saveVector(vectorCampos));
                        for (int i = 0; i < resultado.getNumRows(); i++) {
                            System.out.print("[" + resultado.get(i, 0) + "]");
                            textResultado.append("x" + i + "=" + resultado.get(i, 0) + "\n");
                        }
                    });
                    break;

                case "pInterior":
                    var vector1 = generarVector(panelVector);
                    var vector2 = generarVector(panelVector2);
                    btnOperacion.addActionListener(o -> {
                        double resultado = ProductoInterior(saveVector(vector1), saveVector(vector2));
                        System.out.println("Producto Interior: " + resultado);
                        textResultado.append("Producto Interior: " + resultado + "\n");
                    });
                    break;

                case "dQR":
                    generarMatriz();
                    // Aquí se agregaría el código para la descomposición QR
                    break;
            }
        });
    }

    private List<JTextField> generarMatriz(){

        int size=Integer.parseInt(matrizSize.getText());
        List<JTextField> campos=new ArrayList<>();
        double[][][] saveMatriz=null;
        panelMatriz.removeAll();
        panelMatriz.setLayout(new GridLayout(size,size));
        MTitulo.setVisible(true);
        int index=0;
        for (int i = 0; i <size ; i++) {
            for (int j = 0; j < size; j++) {
                JTextField campo=new JTextField(5);
                campos.add(campo);
                panelMatriz.add(campo);

            }
        }
        panelMatriz.revalidate();
        panelMatriz.repaint();

        return campos;
    }
    private List<JTextField> generarVector(JPanel panel){
        int size=Integer.parseInt(vectorSize.getText());
        List<JTextField> campos=new ArrayList<>();
        double[] saveVector=null;
        panel.removeAll();
        panel.setLayout(new GridLayout(1,size));
        VTitulo.setVisible(true);
        int index=0;
        for (int i = 0; i <size ; i++) {

            JTextField campo=new JTextField(5);
            campos.add(campo);
            panel.add(campo);

        }
        panel.revalidate();
        panel.repaint();

        return campos;
    }

    private double[][] saveMatriz(List<JTextField> campos){

        int size=Integer.parseInt(matrizSize.getText());
        double [][] matriz= new double[size][size];
        int index=0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matriz[i][j]=Double.parseDouble(campos.get(index).getText());
                index++;
            }
        }
        for (int i = 0; i < size; i++) {
            System.out.print("f"+i+"= ");
            for (int j = 0; j < size; j++) {
                System.out.print("["+matriz[i][j]+"]");
            }
            System.out.println();
        }
        return matriz;
    }
    private double[] saveVector(List<JTextField> campos){
        int size=Integer.parseInt(vectorSize.getText());
        double[] vector=new double[size];
        for (int i = 0; i < size; i++) {
            vector[i]=Double.parseDouble(campos.get(i).getText());
        }
        System.out.print("x= ");
        for (int i = 0; i < size; i++) {
            System.out.print("["+vector[i]+"]");
        }

        return vector;
    }

    private SimpleMatrix linealTransform(double[][] matriz, double[] vector){

        double[][] matrizObj=matriz;
        double[] vectorObj=vector;
        SimpleMatrix Tmatriz= new SimpleMatrix(matrizObj);
        SimpleMatrix TVector= new SimpleMatrix(vectorObj);

        return Tmatriz.mult(TVector);
    }
    private double ProductoInterior(double[] vector1,double[] vector2){
        double[]vector1Obj=vector1;
        double[]vector2Obj=vector2;
        SimpleMatrix TVector1= new SimpleMatrix(vector1Obj);
        SimpleMatrix TVector2= new SimpleMatrix(vector2Obj);

        return TVector1.dot(TVector2);
    }


    private void initComponents(){
        setContentPane(mainPanel);
        setTitle("Calculadora de Matrices");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {Menu menu=new Menu();}
}
