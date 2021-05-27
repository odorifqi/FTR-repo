/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.DataFileFilter;
import controller.Controller;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Odorifqi
 */
public class Home extends JFrame {
    private TablePanel tablePanel = new TablePanel();
    private JPanel chartPanel = new JPanel();
    private PredictDialog predictDialog;
    private Controller controller = new Controller();
    private ChartPanel cp = new ChartPanel();   
    private MapePanel mapePanel = new MapePanel();
    private JButton input, predict, reset;
    private JFileChooser fileChooser;
    private DetailPanel detailPanel = new DetailPanel();
    private PredictPanel predictPanel = new PredictPanel();
    private JTabbedPane tabPane;
    
    public Home(){
        super("Aplikasi Prediksi Fuzzy Time Series Ruey Chyn Tsaur");
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(cp.EmptyChart());
        
        tabPane = new JTabbedPane();
        tabPane.addTab("Chart", chartPanel);
        tabPane.addTab("Prediction", predictPanel);
        tabPane.addTab("Detail", detailPanel);
        
        tablePanel.setData(controller.getTableData());
        
        Insets inset = new Insets(5, 5, 5, 5);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weighty = 0;
        gc.insets = inset;
        gc.fill = GridBagConstraints.CENTER;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(createButton(), gc);
        
        gc.gridx = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        add(mapePanel, gc);
        
        gc.gridy = 1;
        gc.gridx = 0;
        gc.weightx = 0;
        gc.weighty = 3;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(tablePanel, gc);
        
        gc.gridx++;
        gc.weightx = 1;
        add(tabPane, gc);
        
        setMinimumSize(new Dimension(800,400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private JPanel createButton(){
        JPanel buttonPanel= new JPanel();
        input = new JButton("Input");
        predict = new JButton("Predict");
        reset = new JButton("Reset");
        predict.setEnabled(false);
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new DataFileFilter());

        input.addActionListener((ActionEvent e) -> {
            if (fileChooser.showOpenDialog(Home.this) == JFileChooser.APPROVE_OPTION) {
                try {    
                    controller.inputFile(fileChooser.getSelectedFile()); 
                    tablePanel.refresh();
                    chartPanel.removeAll();
                    chartPanel.add(cp.RawChart(controller.getListDateChart(), controller.getListActualChart(), fileChooser.getSelectedFile()));
                    chartPanel.validate();  
                    
                    mapePanel.setText("");
                    
                    predict.setEnabled(true);
                    detailPanel.setText(null);
                    predictPanel.setText(null);
                } catch (ParseException | IOException | IndexOutOfBoundsException ex) {
                   JOptionPane.showMessageDialog(this, "Can't input file", "error", JOptionPane.ERROR_MESSAGE);
                   reset();
                }
            }
        });
        
        predict.addActionListener((ActionEvent e) -> {
            predictDialog = new PredictDialog(this);
            predictDialog.setVisible(true);
            String choice = predictDialog.getInterval();
            predictDialog.dispose();

            if(choice != null){
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                tablePanel.resetData();
                controller.prediksi(choice);
                
                tablePanel.refresh();

                chartPanel.removeAll();
                chartPanel.add(cp.DefuzzyChart(controller.getListDateChart(), controller.getListActualChart(), controller.getListDefuzzy(), fileChooser.getSelectedFile()));
                chartPanel.validate();
                
                mapePanel.setText(controller.mapeDefuzzy());
                
                try {
                    predictPanel.setText(
                            "Prediction result: " + controller.getPredict() + "\n" + "\n"
                    );
                } catch (ParseException ex) {
                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                detailPanel.setText("Interval length method: " + choice + "\n" + "\n" +
                        "Max Value: " + Double.toString(controller.getMaxValue()) + "\n" + "\n" +
                                     "Min Value: " + Double.toString(controller.getMinValue()) + "\n" + "\n" +
                                     "Interval length: " + Integer.toString(controller.getLength()) + "\n" + "\n" +
                                             "Interval: " + controller.getInterval() + "\n" + "\n" +
                                     "Interval Partition: " + controller.getIntervalPart() + "\n" + "\n" 
                        );
                
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        reset.addActionListener((ActionEvent e) -> {
            reset();
        });
        
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.add(input);
        buttonPanel.add(predict);
        buttonPanel.add(reset);
        
        return buttonPanel;
    }
    
    private void reset(){
        chartPanel.removeAll();
        chartPanel.add(cp.EmptyChart());
        chartPanel.validate();

        mapePanel.setText("");

        tablePanel.resetData();
        tablePanel.refresh();
        
        detailPanel.setText(null);
        predictPanel.setText(null);
        
        predict.setEnabled(false);
        System.gc();
    }
}


