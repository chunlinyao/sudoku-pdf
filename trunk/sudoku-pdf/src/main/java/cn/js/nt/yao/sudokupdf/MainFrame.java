/*
 *  Project: Sudoku PDF
 *
 *  Copyright 2008 yaochunlin.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 * ------------------------------------------------------------------------
 * $Header$
 * $Revision$
 * $Date$
 */
package cn.js.nt.yao.sudokupdf;

import diuf.sudoku.Grid;
import diuf.sudoku.generator.Generator;
import diuf.sudoku.generator.Symmetry;
import diuf.sudoku.gui.AutoBusy;
import diuf.sudoku.solver.Rule;
import diuf.sudoku.solver.Solver;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author yaochunlin
 */
public class MainFrame extends JFrame {

    private Main app;

    MainFrame(Main m) {
        this.app = m;
        initParameters();
        initGUI();
    }
    private static final long serialVersionUID = 1L;
    private JButton btnPrint;
    private JLabel lblGenerated;
    private JSpinner spnPrintCount;
    private EnumSet<Symmetry> symmetries = EnumSet.noneOf(Symmetry.class);
    private Difficulty difficulty = Difficulty.Easy;
    private boolean isExact = true;
    private int printCount = 4;
    private GeneratorThread generator = null;
    private List<Grid> sudokuList = new ArrayList<Grid>();

    private void initParameters() {
        symmetries.add(Symmetry.Orthogonal);
        symmetries.add(Symmetry.BiDiagonal);
        symmetries.add(Symmetry.Rotational180);
        symmetries.add(Symmetry.Rotational90);
        symmetries.add(Symmetry.Full);
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());

        JPanel paramPanel = new JPanel();
        JPanel commandPanel = new JPanel();
        this.add(paramPanel, BorderLayout.CENTER);
        this.add(commandPanel, BorderLayout.SOUTH);

        // Command pane
        commandPanel.setLayout(new GridLayout(1, 2));

        JPanel pnlGenerate = new JPanel();
        pnlGenerate.setLayout(new FlowLayout(FlowLayout.CENTER));
        commandPanel.add(pnlGenerate);

        JPanel pnlClose = new JPanel();
        pnlClose.setLayout(new FlowLayout(FlowLayout.CENTER));
        commandPanel.add(pnlClose);

        btnPrint = new JButton();
        btnPrint.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
        btnPrint.setText("Print");
        btnPrint.setMnemonic(KeyEvent.VK_G);
        btnPrint.setToolTipText(
                "Print out random Sudoku that matches the given parameters");
        btnPrint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (generator == null) {
                    generate();
                } else {
                    stop();
                }
            }
        });
        pnlGenerate.add(btnPrint);
        lblGenerated = new JLabel("");
        lblGenerated.setToolTipText(
                "<html><body><b>Generated</b>&nbsp; count</body></html>");
        pnlGenerate.add(lblGenerated);

        JButton btnClose = new JButton();
        btnClose.setText("Close");
        btnClose.setMnemonic(KeyEvent.VK_C);
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        pnlClose.add(btnClose);

        // Parameters pane
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));

        JPanel symmetryPanel = new JPanel();
        symmetryPanel.setBorder(new TitledBorder("Allowed symmetry types"));
        paramPanel.add(symmetryPanel);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setBorder(new TitledBorder("Difficulty"));
        paramPanel.add(difficultyPanel);

        // Parameters - Symmetry pane
        symmetryPanel.setLayout(new GridLayout(3, 4));

        for (final Symmetry symmetry : Symmetry.values()) {
            final JCheckBox chkSymmetry = new JCheckBox();
            chkSymmetry.setSelected(symmetries.contains(symmetry));
            chkSymmetry.setText(symmetry.toString());
            chkSymmetry.setToolTipText(symmetry.getDescription());
            chkSymmetry.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chkSymmetry.isSelected()) {
                        symmetries.add(symmetry);
                    } else {
                        symmetries.remove(symmetry);
                    }
                }
            });
            symmetryPanel.add(chkSymmetry);
        }

        // Parameters - Difficulty
        difficultyPanel.setLayout(new BorderLayout());

        JPanel diffChooserPanel = new JPanel();
        diffChooserPanel.setLayout(new BoxLayout(diffChooserPanel,
                BoxLayout.X_AXIS));
        difficultyPanel.add(diffChooserPanel, BorderLayout.NORTH);

        final JComboBox selDifficulty = new JComboBox();

        for (Difficulty d : Difficulty.values()) {
            selDifficulty.addItem(d);
        }

        selDifficulty.setToolTipText(
                "Choose the difficulty of the Sudoku to generate");
        selDifficulty.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                difficulty = (Difficulty) selDifficulty.getSelectedItem();
            }
        });
        diffChooserPanel.add(selDifficulty);

        final JRadioButton chkExactDifficulty = new JRadioButton(
                "Exact difficulty");
        chkExactDifficulty.setToolTipText(
                "Generate a Sudoku with exactly the chosen difficulty");
        chkExactDifficulty.setMnemonic(KeyEvent.VK_E);
        chkExactDifficulty.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (chkExactDifficulty.isSelected()) {
                    isExact = true;
                }
            }
        });
        diffChooserPanel.add(chkExactDifficulty);

        final JRadioButton chkMaximumDifficulty = new JRadioButton(
                "Maximum difficulty");
        chkMaximumDifficulty.setToolTipText(
                "Generate a Sudoku with at most the chosen difficulty");
        chkMaximumDifficulty.setMnemonic(KeyEvent.VK_M);
        chkMaximumDifficulty.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (chkMaximumDifficulty.isSelected()) {
                    isExact = false;
                }
            }
        });
        diffChooserPanel.add(chkMaximumDifficulty);

        ButtonGroup group = new ButtonGroup();
        group.add(chkExactDifficulty);
        group.add(chkMaximumDifficulty);
        chkExactDifficulty.setSelected(true);

        // Parameters - options
        JPanel optionPanel = new JPanel();
        optionPanel.setBorder(new TitledBorder(""));
        optionPanel.setLayout(new GridLayout(1, 1));
        paramPanel.add(optionPanel, BorderLayout.NORTH);

        JLabel lblPrintCount = new JLabel();
        lblPrintCount.setText(
                "<html><body><b>Print out sudoku count:</b></body></html>");
        optionPanel.add(lblPrintCount);
        spnPrintCount = new JSpinner();
        spnPrintCount.setValue(printCount);
        spnPrintCount.setToolTipText("Number of print out sudoku.");
        spnPrintCount.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                printCount = (Integer) spnPrintCount.getValue();

                if (printCount < 1) {
                    printCount = 1;
                    spnPrintCount.setValue(printCount);
                }
            }
        });
        optionPanel.add(spnPrintCount);
    }

    private void generate() {
        if (symmetries.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one symmetry", "Generate",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        // Gather parameters
        double minDifficulty = difficulty.getMinDifficulty();
        double maxDifficulty = difficulty.getMaxDifficulty();

        if (!isExact) {
            minDifficulty = 1.0;
        }

        List<Symmetry> symList = new ArrayList<Symmetry>(symmetries);

        // Generate grid
        generator = new GeneratorThread(symList, minDifficulty, maxDifficulty,
                printCount);
        generator.start();
    }

    private void stop() {
        if ((generator != null) && generator.isAlive()) {
            generator.interrupt();

            try {
                generator.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        generator = null;
    }

    private void printOut() {
        if (sudokuList.size() >= printCount) {
            new Thread() {

                @Override
                public void run() {
                    List<PrintRecord> printList = new ArrayList<PrintRecord>();

                    for (Grid g : new ArrayList<Grid>(sudokuList)) {
                        Grid copy = new Grid();
                        g.copyTo(copy);

                        Solver solver = new Solver(copy);
                        Map<Rule, Integer> ruleMap = null;

                        try {
                            solver.rebuildPotentialValues();
                            ruleMap = solver.solve(null);
                        } catch (UnsupportedOperationException ex) {
                        }

                        PrintRecord pp = new PrintRecord(g, copy, ruleMap);
                        printList.add(pp);
                    }

                    File f = app.createPDF(printList);
                    try {
                        java.awt.Desktop.getDesktop().open(f);
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }
    }

    private void close() {
        stop();
        this.dispose();;
    }

    /**
     * Thread that generates a mew grid.
     */
    private class GeneratorThread extends Thread {

        private final List<Symmetry> symmetries;
        private final double minDifficulty;
        private final double maxDifficulty;
        private int curSize;
        private Generator generator;
        private int printCount;

        public GeneratorThread(List<Symmetry> symmetries, double minDifficulty,
                double maxDifficulty, int printCount) {
            this.symmetries = symmetries;
            this.minDifficulty = minDifficulty;
            this.maxDifficulty = maxDifficulty;
            this.printCount = printCount;
            curSize = sudokuList.size();
        }

        @Override
        public void interrupt() {
            generator.interrupt();
        }

        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    AutoBusy.setBusy(MainFrame.this, true);
                    AutoBusy.setBusy(btnPrint, false);
                    btnPrint.setText("Stop");
                }
            });
            generator = new Generator();

            for (int i = curSize; i < printCount; i++) {
                final Grid result = generator.generate(symmetries,
                        minDifficulty, maxDifficulty);
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        if (result != null) {
                            sudokuList.add(result);
                            lblGenerated.setText(String.format(
                                    "<html><body>&nbsp;&nbsp;%d&nbsp;<i>Generated</i></body></html>",
                                    sudokuList.size()));
                        }
                    }
                });
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    if (MainFrame.this.isVisible()) {
                        AutoBusy.setBusy(MainFrame.this, false);
                        btnPrint.setText("Print");
                        MainFrame.this.printOut();
                    }
                }
            });
            MainFrame.this.generator = null;
        }
    }

    private enum Difficulty {

        Easy(1.0,
        1.2), Medium(1.3, 1.5), Hard(1.6, 2.5), Fiendish(2.6, 6.0),
        Diabolical(6.1, 11.0);
        private final double maxDificulty;
        private final double minDifficulty;

        private Difficulty(double min, double max) {
            this.minDifficulty = min;
            this.maxDificulty = max;
        }

        public double getMinDifficulty() {
            return this.minDifficulty;
        }

        public double getMaxDifficulty() {
            return this.maxDificulty;
        }
    }
}
