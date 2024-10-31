package calculadora;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.simple.SimpleMatrix;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

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
    private JPanel panelGrafica;
    private String op="";

    public Menu(){
        initComponents();
        matrizSize.setEnabled(false);
        vectorSize.setEnabled(false);
        btnTLineales.addActionListener(e->{
            panelMatriz.removeAll();
            panelMatriz.repaint();
            panelVector.removeAll();
            panelVector.repaint();
            panelVector2.removeAll();
            panelVector2.repaint();
            panelGrafica.removeAll();
            panelGrafica.repaint();
            matrizSize.setEnabled(true);
            vectorSize.setEnabled(true);
            btnOperacion.setVisible(false);
            matrizSize.setText("");
            vectorSize.setText("");
            textResultado.setText("");
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            op="tLineal";
        });
        btnPinterior.addActionListener(e->{
            matrizSize.setEnabled(false);
            panelMatriz.removeAll();
            panelMatriz.repaint();
            panelVector.removeAll();
            panelVector.repaint();
            panelVector2.removeAll();
            panelVector2.repaint();
            panelGrafica.removeAll();
            panelGrafica.repaint();
            matrizSize.setEnabled(true);
            vectorSize.setEnabled(true);
            btnOperacion.setVisible(false);
            matrizSize.setText("");
            vectorSize.setText("");
            textResultado.setText("");
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            vectorSize.setEnabled(true);
            op="pInterior";
        });
        btnDQr.addActionListener(e->{
            vectorSize.setEnabled(false);
            panelMatriz.removeAll();
            panelMatriz.repaint();
            panelVector.removeAll();
            panelVector.repaint();
            panelVector2.removeAll();
            panelVector2.repaint();
            panelGrafica.removeAll();
            panelGrafica.repaint();
            matrizSize.setEnabled(true);
            vectorSize.setEnabled(true);
            btnOperacion.setVisible(false);
            matrizSize.setText("");
            vectorSize.setText("");
            textResultado.setText("");
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            matrizSize.setEnabled(true);
            op="dQr";
        });
        btnGenerate.addActionListener(a -> {
            btnOperacion.setVisible(true);

            switch (op) {
                case "tLineal":
                    var matrizCampos = generarMatriz();
                    var vectorCampos = generarVector(panelVector);
                    btnOperacion.addActionListener(o -> {
                        var matrizOriginal=saveMatriz(matrizCampos);
                        var vectorOriginal=saveVector(vectorCampos);
                        var resultado = linealTransform(matrizOriginal,vectorOriginal);
                        double[] transformacion=new double[resultado.getNumRows()];
                        for (int i = 0; i < resultado.getNumRows(); i++) transformacion[i]= resultado.get(i,0);
                        for (int i = 0; i < transformacion.length; i++) {
                            System.out.print("x"+i+"=");
                            System.out.print("[" + transformacion[i] + "]");
                            textResultado.append("x" + i + "=" + transformacion[i] + "\n");
                        }
                        XYChart chart=graficarTLineal(vectorOriginal,transformacion);
                        XChartPanel<XYChart> panel=new XChartPanel<>(chart);
                        panelGrafica.add(panel);
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

                case "dQr":
                    var matriz=generarMatriz();
                    btnOperacion.addActionListener(o->{
                        double[][]Q=null;
                        double[][]R=null;
                        double[][]QR=null;
                        var objQr= descomposicionQR(saveMatriz(matriz));
                        Q= (double[][]) objQr[0];
                        R= (double[][]) objQr[1];
                        System.out.println("matriz q:");
                        textResultado.append("Matriz Q: \n");
                        for (int i = 0; i <Q.length ; i++) {
                            textResultado.append("(");
                            for (int j = 0; j < Q[i].length ; j++) {
                                textResultado.append(String.format("%.2f",Q[i][j]));
                                if (j < Q[i].length - 1) textResultado.append(" , ");
                                System.out.print("["+String.format("%.2f",Q[i][j])+"]");

                            }
                            textResultado.append(")\n");
                            System.out.println();

                        }
                        System.out.println("R:");
                        textResultado.append("Matriz R: \n");
                        for (int i = 0; i <R.length ; i++) {
                            textResultado.append("(");
                            for (int j = 0; j < R[i].length ; j++) {
                                textResultado.append(String.format("%.2f",R[i][j]));
                                if (j < Q[i].length - 1) textResultado.append(" , ");
                                System.out.print("["+String.format("%.2f",R[i][j])+"]");
                            }
                            textResultado.append(")\n");
                            System.out.println();
                        }
                        XYChart chart=graficarDQr(Q,R);
                        XChartPanel<XYChart> panel=new XChartPanel<>(chart);
                        panelGrafica.add(panel);
                    });
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
        System.out.println();
        return vector;
    }

    private SimpleMatrix linealTransform(double[][] matriz, double[] vector){

        SimpleMatrix Tmatriz= new SimpleMatrix(matriz);
        SimpleMatrix TVector= new SimpleMatrix(vector);

        return Tmatriz.mult(TVector);
    }
    private double ProductoInterior(double[] vector1,double[] vector2){
        SimpleMatrix TVector1= new SimpleMatrix(vector1);
        SimpleMatrix TVector2= new SimpleMatrix(vector2);

        return TVector1.dot(TVector2);
    }
    private Object[] descomposicionQR(double[][] matriz){

        SimpleMatrix matrix= new SimpleMatrix(matriz);
        QRDecomposition<DMatrixRMaj> qrDecomposition= DecompositionFactory_DDRM.qr(matrix.getNumRows(),matrix.getNumCols());

        if (!qrDecomposition.decompose(matrix.getDDRM())) {
            throw new RuntimeException("La descomposición QR falló.");
        }

        DMatrixRMaj Q = qrDecomposition.getQ(null, false);
        DMatrixRMaj R = qrDecomposition.getR(null, false);

        double[][] matrizQ=new double[Q.numRows][Q.numCols];
        double[][] matrizR=new double[R.numRows][R.numCols];

        for (int i = 0; i < Q.numRows; i++) {
            for (int j = 0; j < Q.numCols; j++) {
                matrizQ[i][j]=Q.get(i,j);
            }
        }
        for (int i = 0; i < R.numRows; i++) {
            for (int j = 0; j < R.numCols; j++) {
                matrizR[i][j]=R.get(i,j);
            }
        }

        return new Object[]{matrizQ,matrizR};
    }

    private XYChart graficarTLineal(double[] vectorOriginal,double[] vectorTransformado){

        XYChart chart= new XYChartBuilder().width(panelGrafica.getWidth()).height(panelGrafica.getHeight()).
                title("Grafica T. Lineal").xAxisTitle("X").yAxisTitle("Y").build();

        // Vector original
        chart.addSeries("Vector Original", new double[] {0, vectorOriginal[0]},
                new double[] {0, vectorOriginal[vectorOriginal.length-1]});

        // Vector transformado
        chart.addSeries("Vector Transformado", new double[] {0, vectorTransformado[0]},
                new double[] {0, vectorTransformado[vectorTransformado.length-1]});

        return chart;
    }
    private XYChart graficarDQr(double[][] Q,double[][] R){
        XYChart chart = new XYChartBuilder()
                .width(panelGrafica.getWidth())
                .height(panelGrafica.getHeight())
                .title("Grafica DQr")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();

        SimpleMatrix matrizQ = new SimpleMatrix(Q);
        SimpleMatrix matrizR = new SimpleMatrix(R);

        // Base estándar en la misma dimensión que las matrices Q y R
        SimpleMatrix baseEstandar = SimpleMatrix.identity(Q.length);

        // Aplicar Q a la base estándar
        SimpleMatrix baseTransformadaQ = matrizQ.mult(baseEstandar);

        // Graficar cada vector transformado por Q
        for (int i = 0; i < baseTransformadaQ.numCols(); i++) {
            double x = baseTransformadaQ.get(0, i); // Componente X del vector transformado
            double y = baseTransformadaQ.get(1, i); // Componente Y del vector transformado

            chart.addSeries("Vector Q" + (i + 1), new double[] {0, x}, new double[] {0, y});
        }

        // Aplicar R a la base estándar (o a baseTransformadaQ si quieres la transformación completa)
        SimpleMatrix baseTransformadaR = matrizR.mult(baseEstandar);

        // Graficar cada vector transformado por R
        for (int i = 0; i < baseTransformadaR.numCols(); i++) {
            double x = baseTransformadaR.get(0, i); // Componente X del vector transformado
            double y = baseTransformadaR.get(1, i); // Componente Y del vector transformado

            chart.addSeries("Vector R" + (i + 1), new double[] {0, x}, new double[] {0, y});
        }

        return chart;
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
