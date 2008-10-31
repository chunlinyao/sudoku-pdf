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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 *
 * @author yaochunlin
 */
final class AboutDialog extends JDialog {
    private static final String VERSION = "1.0";
    private JFrame frame;

    public AboutDialog(JFrame owner) {
        super(owner);
        frame = owner;
        init();
    }

    private void init() {
        setSize(new Dimension(300, 200));
        setResizable(false);
        setTitle("About Sudoku PDF");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createAboutPanel());

        final JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AboutDialog.this.setVisible(false);
                    AboutDialog.this.dispose();
                }
            });

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPane.add(okButton);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        pack();

        addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    AboutDialog.this.setVisible(false);
                    AboutDialog.this.dispose();
                }
            });
        setLocation(frame.getLocation().x
            + ((frame.getWidth() - getWidth()) / 2),
            frame.getLocation().y + ((frame.getHeight() - getHeight()) / 2));
    }

    private JPanel createAboutPanel() {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));

        JLabel lblMe = new JLabel(String.format("<html><body><font size=+2>"
                    + "Sudoku PDF - %s </font><br /> Developed by <b>Yao Chunlin</b>.<br />"
                    + "Web site <a>http://code.google.com/p/sudoku-pdf</a><br />"
                    + "</body></html>", VERSION));
        aboutPanel.add(lblMe);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblThird = new JLabel("<html><body> This software "
                + "include iText and SudokuExplainer.<br /></body></html>");
        aboutPanel.add(lblThird);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblItext = new JLabel("<html><body><font size=+1>iText</font>"
                + "<br /><a>http://www.lowagie.com/iText/</a><br />"
                + "Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie."
                + " <br /></body></html>");
        aboutPanel.add(lblItext);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblSudoku = new JLabel(
                "<html><body><font size=+1>SudokuExplainer</font>"
                + "<br /><a>http://diuf.unifr.ch/people/juillera/Sudoku/Sudoku.html</a>"
                + "<br />Copyright (C) 2006-2007 Nicolas Juillerat"
                + " <br /></body></html>");
        aboutPanel.add(lblSudoku);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblFont = new JLabel(
                "<html><body><font size=+1>URWGothicL-Demi font</font>"
                + "<br /><a>http://www.urwpp.de/deutsch/home.html</a>"
                + "<br />Copyright (URW)++,Copyright 1999 by (URW)++ Design & Development; "
                + "<br />Cyrillic glyphs added by Valek Filippov (C) 2001-2005"
                + " <br /></body></html>");
        aboutPanel.add(lblFont);
        aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return aboutPanel;
    }
}
