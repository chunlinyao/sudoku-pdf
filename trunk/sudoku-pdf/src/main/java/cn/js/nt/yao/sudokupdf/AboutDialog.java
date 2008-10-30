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
        getContentPane().add(new JLabel("Sudoku PDF 1.0-SNAPSHOT"), BorderLayout.CENTER);
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
        setLocation(frame.getLocation().x + (frame.getWidth() - getWidth()) / 2,
                frame.getLocation().y + (frame.getHeight() - getHeight()) / 2);
    }
}
