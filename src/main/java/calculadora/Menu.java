package calculadora;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.decomposition.qr.QRDecompositionHouseholder_DDRM;
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
    private JSpinner vectorSize;
    private JSpinner matrizSize;
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
    private JScrollPane scrollMatriz;
    private JScrollPane scrolllVector;
    private JScrollPane scrollVector2;
    private String op="";

    public Menu(){

        initComponents();
        btnTLineales.addActionListener(e->{
           limpiarCampos();
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            op="tLineal";

        });
        btnPinterior.addActionListener(e->{
            limpiarCampos();
            matrizSize.setEnabled(false);
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            vectorSize.setEnabled(true);
            op="pInterior";
        });
        btnDQr.addActionListener(e->{
           limpiarCampos();
            vectorSize.setEnabled(false);
            MTitulo.setVisible(false);
            VTitulo.setVisible(false);
            matrizSize.setEnabled(true);
            op="dQr";
        });
        btnGenerate.addActionListener(a -> {

            // Remover listeners previos de btnOperacion
            for (ActionListener listener : btnOperacion.getActionListeners()) {
                btnOperacion.removeActionListener(listener);
            }

            switch (op) {
                case "tLineal":
                    if(vectorSize.getValue().equals(matrizSize.getValue())){
                        btnOperacion.setVisible(true);
                        try{
                            scrollMatriz.setVisible(true);
                            var matrizCampos = generarMatriz();
                            scrolllVector.setVisible(true);
                            var vectorCampos = generarVector(panelVector);
                            btnOperacion.addActionListener(ot -> {
                                panelGrafica.removeAll();
                                panelGrafica.repaint();
                                textResultado.setText("");

                                var resultadoT = linealTransform(saveMatriz(matrizCampos),saveVector(vectorCampos));
                                double[] transformacion=new double[resultadoT.getNumRows()];
                                for (int i = 0; i < resultadoT.getNumRows(); i++) transformacion[i]= resultadoT.get(i,0);
                                textResultado.append("x=(");
                                for (int i = 0; i < transformacion.length; i++) {
                                    System.out.print("[" + transformacion[i] + "]");
                                    textResultado.append(String.valueOf(transformacion[i]));
                                    if (i < transformacion.length-1) textResultado.append(" , ");
                                }
                                textResultado.append(")");

                                XYChart chartT=graficarTLineal(saveVector(vectorCampos),transformacion);
                                XChartPanel<XYChart> panelT=new XChartPanel<>(chartT);
                                panelGrafica.add(panelT);
                            });
                        }catch (Exception e){
                            JOptionPane.showOptionDialog(this,"Error, solo se admiten numeros",
                                    "Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                        }
                    }else{
                        JOptionPane.showOptionDialog(this,"El vector y la matriz deben ser de igual tamaño",
                                null,JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    }
                    break;

                case "pInterior":
                    try{
                        scrolllVector.setVisible(true);
                        var vector1 = generarVector(panelVector);
                        scrollVector2.setVisible(true);
                        var vector2 = generarVector(panelVector2);
                        btnOperacion.setVisible(true);
                        btnOperacion.addActionListener(op -> {
                            panelGrafica.removeAll();
                            panelGrafica.repaint();
                            textResultado.setText("");
                            double resultadoP = ProductoInterior(saveVector(vector1),saveVector(vector2));
                            System.out.println("Producto Interior: " + resultadoP);
                            textResultado.append("Producto Interior: " + resultadoP + "\n");
                            XYChart chartP=graficarPInterno(saveVector(vector1),saveVector(vector2),resultadoP);
                            XChartPanel<XYChart> panelP=new XChartPanel<>(chartP);
                            panelGrafica.add(panelP);
                        });
                    }catch (Exception e){
                        JOptionPane.showOptionDialog(this,"Error, solo se admiten numeros",
                                null,JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    }
                    break;

                case "dQr":
                    btnOperacion.setVisible(true);
                    try{
                        scrollMatriz.setVisible(true);
                        var matrizCampos=generarMatriz();
                        btnOperacion.addActionListener(od->{
                            panelGrafica.removeAll();
                            panelGrafica.repaint();
                            textResultado.setText("");
                            var matriz=saveMatriz(matrizCampos);

                            double[][]Q=null;
                            double[][]R=null;
                            try{
                                var objQr= descomposicionQR(matriz);
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

                                XYChart chartDqr=graficarDQr(Q,R);
                                XChartPanel<XYChart> panelDqr=new XChartPanel<>(chartDqr);
                                panelGrafica.add(panelDqr);
                            }catch (RuntimeException e){
                                JOptionPane.showOptionDialog(this,e.getMessage(),
                                        "Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                            }
                        });
                    }catch (Exception e){
                        JOptionPane.showOptionDialog(this,"Error, solo se admiten numeros",
                                "Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
                    }
                    break;
            }
        });
    }

    private void limpiarCampos(){
        textResultado.setText("");
        panelGrafica.removeAll();
        panelGrafica.repaint();
        panelMatriz.removeAll();
        panelMatriz.repaint();
        scrollMatriz.setVisible(false);
        panelVector.removeAll();
        panelVector.repaint();
        scrolllVector.setVisible(false);
        panelVector2.removeAll();
        panelVector2.repaint();
        scrollVector2.setVisible(false);
        matrizSize.setEnabled(true);
        vectorSize.setEnabled(true);
        btnOperacion.setVisible(false);
        matrizSize.setValue(0);
        vectorSize.setValue(0);
    }
    private List<JSpinner> generarMatriz(){

        List<JSpinner> campos=new ArrayList<>();

        int size=(int) matrizSize.getValue();
        double[][][] saveMatriz=null;
        panelMatriz.removeAll();

        panelMatriz.setLayout(new GridLayout(size,size));
        MTitulo.setVisible(true);
        int index=0;
        for (int i = 0; i <size ; i++) {
            for (int j = 0; j < size; j++) {
                JSpinner campo=new JSpinner(new SpinnerNumberModel(0,0,100,1));
                campos.add(campo);
                panelMatriz.add(campo);

            }
        }
        panelMatriz.revalidate();
        panelMatriz.repaint();



        return campos;
    }
    private List<JSpinner> generarVector(JPanel panel){

    List<JSpinner> campos=new ArrayList<>();
    int size=(int)vectorSize.getValue();
    double[] saveVector=null;
    panel.removeAll();
        panel.setLayout(new GridLayout(1,size));
        VTitulo.setVisible(true);
        int index=0;
        for (int i = 0; i <size ; i++) {

            JSpinner campo=new JSpinner(new SpinnerNumberModel(0,0,100,1));
            campos.add(campo);
            panel.add(campo);

        }
        panel.revalidate();
        panel.repaint();

        return campos;
    }

    private double[][] saveMatriz(List<JSpinner> campos){

        int size=(int)matrizSize.getValue();
        double [][] matriz= new double[size][size];
        int index=0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matriz[i][j]=(double)(int)campos.get(i).getValue();
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
    private double[] saveVector(List<JSpinner> campos){
        int size=(int) vectorSize.getValue();
        double[] vector=new double[size];
        for (int i = 0; i < size; i++) {
            vector[i]=(double)(int) campos.get(i).getValue();
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
        QRDecomposition<DMatrixRMaj> qrDecomposition= new QRDecompositionHouseholder_DDRM();
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
    private XYChart graficarPInterno(double[] vector1,double[]vector2, double Pinterno){

        XYChart chart=new XYChartBuilder().width(panelGrafica.getWidth()).height(panelGrafica.getHeight()).
                title("Grafica P. Internol").xAxisTitle("X").yAxisTitle("Y").build();


        chart.addSeries("vector A",new double[]{0,vector1[vector1.length-1]},new double[]{0,vector1[vector1.length-1]});
        chart.addSeries("vector B",new double[]{0,vector2[vector2.length-1]},new double[]{0,vector2[vector2.length-1]});
        chart.setTitle("Producto interno: "+Pinterno);
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
        setSize(850,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        FlatMacLightLaf.setup();
        Menu menu=new Menu();}
}
