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
 * $Id$
 * $Revision$
 * $Date$
 */
package cn.js.nt.yao.sudokupdf;

import diuf.sudoku.Grid;
import diuf.sudoku.generator.Generator;
import diuf.sudoku.generator.Symmetry;
import diuf.sudoku.solver.Rule;
import diuf.sudoku.solver.Solver;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author yaochunlin
 */
public class MainFrame extends JFrame implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private Main app;
    private JProgressBar progress;
    private JButton btnPrint;
    private JLabel lblGenerated;
    private JSpinner spnPrintCount;
    private EnumSet<Symmetry> symmetries = EnumSet.noneOf(Symmetry.class);
    private Difficulty difficulty = Difficulty.Easy;
    private boolean isExact = true;
    private int amount = 4;
    private GeneratorThread generatorThread = null;
    private List<Grid> sudokuList = new ArrayList<Grid>();
    private JLabel lblDifficulty;
    private String pdfLayout;

    MainFrame(Main m) {
        super("Sudoku PDF - Developed by Yao Chunlin");
        this.app = m;
        initParameters();
        initGUI();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MainFrame.this.close();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void initButtonPane(JPanel commandPanel) {
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
        btnPrint.setMnemonic(KeyEvent.VK_P);
        btnPrint.setToolTipText(
                "Print out random Sudoku that matches the given parameters");
        btnPrint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (generatorThread == null) {
                    generate();
                }
            }
        });
        pnlGenerate.add(btnPrint);

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
    }

    private void initGlassPane() {
        // Glass Pane ,disable input when processing
        progress = new JProgressBar();

        JPanel statusPane = new JPanel(new GridLayout(1, 4));
        statusPane.setOpaque(true);
        statusPane.add(new JLabel("Please wait..."));
        statusPane.add(progress);
        lblGenerated = new JLabel("");
        lblGenerated.setToolTipText(
                "<html><body><b>Generated</b>&nbsp; count</body></html>");
        statusPane.add(lblGenerated);

        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (generatorThread != null) {
                    MainFrame.this.stop();
                }
            }
        });
        statusPane.add(cancelButton);

        final List<JComponent> enabledWhenBusy = new ArrayList<JComponent>();
        final BusyGlassPanel busyGlassPanel = new BusyGlassPanel(this,
                enabledWhenBusy);

        busyGlassPanel.setLayout(new BorderLayout());
        busyGlassPanel.setOpaque(false);
        busyGlassPanel.add(statusPane, BorderLayout.SOUTH);
        this.setGlassPane(busyGlassPanel);
    }

    private void initMenu() {
        final JMenuBar jMenuBar = new JMenuBar();
        JMenuItem mi;
        final JMenu fileMenu = jMenuBar.add(new JMenu("File"));
        fileMenu.setMnemonic('F');
        mi = fileMenu.add(new JMenuItem("Close"));
        mi.setMnemonic('X');
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.close();
            }
        });

        final JMenu helpMenu = jMenuBar.add(new JMenu("Help"));
        fileMenu.setMnemonic('H');
        mi = helpMenu.add(new JMenuItem("About"));
        mi.setMnemonic('B');
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new AboutDialog(MainFrame.this);
                dialog.setVisible(true);
            }
        });
        this.setJMenuBar(jMenuBar);
    }

    private void initParamPane(JPanel paramPanel) {
        // Parameters pane
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));

        JPanel symmetryPanel = new JPanel();
        symmetryPanel.setBorder(new TitledBorder("Allowed symmetry types"));
        paramPanel.add(symmetryPanel);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setBorder(new TitledBorder("Difficulty"));
        paramPanel.add(difficultyPanel);
        // Parameters - Symmetry pane
        symmetryPanel.setLayout(new GridLayout(2, 5));

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
                refreshLblDifficulty();
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
                    refreshLblDifficulty();
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
                    refreshLblDifficulty();
                }
            }
        });
        diffChooserPanel.add(chkMaximumDifficulty);

        ButtonGroup group = new ButtonGroup();
        group.add(chkExactDifficulty);
        group.add(chkMaximumDifficulty);
        chkExactDifficulty.setSelected(true);

        JPanel descriptPane = new JPanel();
        descriptPane.setBorder(new TitledBorder("Difficulty Rating"));
        descriptPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        lblDifficulty = new JLabel("");
        refreshLblDifficulty();
        descriptPane.add(lblDifficulty);
        difficultyPanel.add(descriptPane, BorderLayout.CENTER);
    }

    private void refreshLblDifficulty() {
        double minrate = isExact ? difficulty.getMinDifficulty() : 1.0;
        lblDifficulty.setText(String.format(
                "<html><body>Will generate puzzle between rate <b>%4.1f</b> and <b>%4.1f</b>.</body></html>",
                minrate, difficulty.getMaxDifficulty()));
    }

    private void initParameters() {
        symmetries.add(Symmetry.Orthogonal);
        symmetries.add(Symmetry.BiDiagonal);
        symmetries.add(Symmetry.Rotational180);
        symmetries.add(Symmetry.Rotational90);
        symmetries.add(Symmetry.Full);
    }

    private void initGUI() {
        initMenu();
        this.setLayout(new BorderLayout());

        JPanel paramPanel = new JPanel();
        JPanel buttonPane = new JPanel();
        this.add(paramPanel, BorderLayout.CENTER);
        this.add(buttonPane, BorderLayout.SOUTH);
        initButtonPane(buttonPane);
        initParamPane(paramPanel);
        initPrintPane(paramPanel);
        initGlassPane();
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

        if (sudokuList.size() > 0) {
            String[] options = {"Resume", "Restart", "Cancel"};
            int result = JOptionPane.showOptionDialog(this,
                    "Resume the previouse job.", "Resume or restart",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (result) {
                case 0:
                    break;

                case 1:
                    sudokuList.clear();

                    break;

                case 2:
                    return;
            }
        }

        progress.setMaximum(amount);
        progress.setMinimum(0);
        // Generate grid
        generatorThread = new GeneratorThread(symList, minDifficulty,
                maxDifficulty, amount);
        generatorThread.addPropertyChangeListener(this);
        generatorThread.execute();
    }

    private void initPrintPane(JPanel paramPanel) {
        // Parameters - print
        JPanel printPanel = new JPanel();
        printPanel.setBorder(new TitledBorder("Print option"));
        printPanel.setLayout(new BoxLayout(printPanel, BoxLayout.X_AXIS));
        paramPanel.add(printPanel, BorderLayout.NORTH);

        JLabel lblPrintCount = new JLabel();
        lblPrintCount.setText("Amount of puzzles:");
        printPanel.add(lblPrintCount);
        spnPrintCount = new JSpinner(new SpinnerNumberModel(4, 1,
                Integer.MAX_VALUE, 1));
        lblPrintCount.setLabelFor(spnPrintCount);
        spnPrintCount.setValue(amount);
        spnPrintCount.setToolTipText("Amount of puzzles");
        spnPrintCount.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                amount = (Integer) spnPrintCount.getValue();
            }
        });
        printPanel.add(spnPrintCount);

        JLabel lblLayout = new JLabel();
        lblLayout.setText("Page:");
        printPanel.add(lblLayout);

        final JComboBox layoutCB = new JComboBox();
        lblLayout.setLabelFor(layoutCB);
        layoutCB.addItem("A4");
        layoutCB.addItem("Letter");
        layoutCB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.pdfLayout = (String) layoutCB.getSelectedItem();
            }
        });
        printPanel.add(layoutCB);
    }

    private void stop() {
        if ((generatorThread != null)) {
            generatorThread.interrupt();
        }
    }

    private void printOut() {
        if (sudokuList.size() >= amount) {
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

                    File f = app.createPDF(printList, pdfLayout);

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
        this.dispose();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            if (evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                getGlassPane().setVisible(true);
            } else if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                if (this.isVisible()) {
                    this.getGlassPane().setVisible(false);
                }

                try {
                    sudokuList = (List<Grid>) generatorThread.get();

                    if (sudokuList.size() >= amount) {
                        this.printOut();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                generatorThread = null;
            }
        }
    }

    /**
     * Thread that generates a mew grid.
     */
    private class GeneratorThread extends SwingWorker<List<Grid>, Grid> {

        private final List<Symmetry> symmetries;
        private final double minDifficulty;
        private final double maxDifficulty;
        private int curSize;
        private Generator generator;
        private int printCount;
        private boolean canceled = false;
        private List<Grid> resultList = new ArrayList<Grid>();

        public GeneratorThread(List<Symmetry> symmetries, double minDifficulty,
                double maxDifficulty, int printCount) {
            this.symmetries = symmetries;
            this.minDifficulty = minDifficulty;
            this.maxDifficulty = maxDifficulty;
            this.printCount = printCount;
            resultList.addAll(sudokuList);
            curSize = resultList.size();
        }

        public void interrupt() {
            this.canceled = true;
            this.generator.interrupt();
        }

        @Override
        protected void process(List<Grid> chunks) {
            resultList.addAll(chunks);
            lblGenerated.setText(String.format(
                    "<html><body>&nbsp;&nbsp;%d&nbsp;<i>Generated</i></body></html>",
                    resultList.size()));
            progress.setValue(resultList.size());
        }

        @Override
        protected List<Grid> doInBackground() throws Exception {
            generator = new Generator();

            for (int i = curSize; i < printCount; i++) {
                final Grid result = generator.generate(symmetries,
                        minDifficulty, maxDifficulty);

                if (result != null) {
                    publish(result);
                }

                if (this.canceled == true) {
                    break;
                }
            }

            return resultList;
        }
    }
    
    private enum Difficulty {

        Easy(1.0, 1.2), Medium(1.3, 1.5), Hard(1.6, 2.5), Fiendish(2.6, 6.0),
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
