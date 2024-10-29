package gui;

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
    private JButton btnOperacon;
    private JTextArea textArea1;

    public Menu(){
        initComponents();
        matrizSize.setEnabled(false);
        vectorSize.setEnabled(false);
        btnTLineales.addActionListener(e->{
            matrizSize.setEnabled(true);
            vectorSize.setEnabled(true);
            btnGenerate.addActionListener(a->{
                var matrizCampos=generarMatriz();
                var vectorCampos= generarVector(panelVector);
                btnOperacon.setVisible(true);
                btnOperacon.addActionListener(o->{
                    var resultado= linealTransform(saveMatriz(matrizCampos),saveVector(vectorCampos));
                    for (int i = 0; i <resultado.getNumRows() ; i++) {
                        System.out.print("["+resultado.get(i,0)+"]");
                    }
                });
            });
        });
        btnPinterior.addActionListener(e->{
            vectorSize.setEnabled(true);
            btnGenerate.addActionListener(a->{
                generarVector(panelVector);
                generarVector(panelVector2);
                btnOperacon.setVisible(true);
            });
        });
        btnDQr.addActionListener(e->{
            matrizSize.setEnabled(true);
            btnGenerate.addActionListener(a->{
                generarMatriz();
                btnOperacon.setVisible(true);
            });
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

        double[][] matrizObj=matriz;
        double[] vectorObj=vector;
        SimpleMatrix Tmatriz= new SimpleMatrix(matrizObj);
        SimpleMatrix TVector= new SimpleMatrix(vectorObj);

        SimpleMatrix transforVector= Tmatriz.mult(TVector);
        return transforVector;
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
