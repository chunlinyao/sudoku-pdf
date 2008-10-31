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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import diuf.sudoku.Grid;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author yaochunlin
 */
class PDFRender {
    private File pdfFile;
    private List<PrintRecord> printList;
    private Document doc;
    private PdfWriter writer;
    private Graphics2D g;
    private Layout layout;

    public PDFRender(Layout layout) {
        this.layout = layout;
    }

    void setData(List<PrintRecord> printList) {
        this.printList = printList;
    }

    File process() {
        openDocument();
        renderSudoku();
        renderAnswer();
        closeDoc();

        return getPdfFile();
    }

    private void closeDoc() {
        getDoc().close();
    }

    private void drawClue(Graphics2D graphic, int r, int c, int v) {
        int x = ((c * 54) + 60) - 36;
        int y = ((r * 54) + 20) - 14;
        graphic.drawString(String.valueOf(v), x, y);
    }

    private void drawGrid(Graphics2D graphic, int index, boolean isAnswer) {
        GeneralPath path = new GeneralPath();
        double x;
        double y;
        graphic.setFont(new Font("Arial", Font.PLAIN, 24));

        if (isAnswer == true) {
            graphic.drawString(String.format("Sudoku #%d", index + 1), 250, 8);
        } else {
            graphic.drawString(String.format("Sudoku #%d", index + 1), 250, 8);
            graphic.drawString(String.format("Difficulty rating: %s",
                    String.valueOf(getPrintList().get(index).getDifficulty())),
                330, 540);

            //g.drawString("49 x Hidden Single",330,545);
        }

        path.moveTo(x = 60, y = 74);

        for (int i = 0; i < 8; i++) {
            path.lineTo(x += 486, y);
            path.moveTo(x -= 486, y += 54);
        }

        path.moveTo(x = 114, y = 20);

        for (int i = 0; i < 8; i++) {
            path.lineTo(x, y += 486);
            path.moveTo(x += 54, y -= 486);
        }

        graphic.setStroke(new BasicStroke(0f));
        graphic.draw(path);
        path = new GeneralPath();
        path.moveTo(x = 60, y = 182);

        for (int i = 0; i < 2; i++) {
            path.lineTo(x += 486, y);
            path.moveTo(x -= 486, y += 162);
        }

        path.moveTo(x = 222, y = 20);

        for (int i = 0; i < 2; i++) {
            path.lineTo(x += 0, y += 486);
            path.moveTo(x += 162, y -= 486);
        }

        graphic.setStroke(new BasicStroke(3f));
        graphic.draw(path);
        path = new GeneralPath();
        path.moveTo(x = 60, y = 20);
        path.lineTo(x += 486, y);
        path.lineTo(x, y += 486);
        path.lineTo(x -= 486, y);
        path.closePath();
        graphic.setStroke(new BasicStroke(4f));
        graphic.draw(path);
    }

    private Document getDoc() {
        return this.doc;
    }

    private Graphics2D getGraphic() {
        if (g == null) {
            g = getWriter().getDirectContent()
                    .createGraphics(getLayout().getPageSize().getWidth(),
                    getLayout().getPageSize().getHeight(), new PDFFontMapper());
        }

        return g;
    }

    public void newPage() {
        if (g != null) {
            g.dispose();
        }

        g = null;
        getDoc().newPage();
    }

    private void openDocument() {
        try {
            if (doc != null) {
                return;
            }

            doc = new Document();
            doc.setPageSize(getLayout().getPageSize());
            pdfFile = File.createTempFile("sudoku", ".pdf");
            getPdfFile().deleteOnExit();
            writer = PdfWriter.getInstance(doc,
                    new BufferedOutputStream(new FileOutputStream(getPdfFile())));
            getWriter().setPdfVersion(PdfWriter.VERSION_1_4);
            getWriter().setViewerPreferences(PdfWriter.PageModeUseNone);
            getDoc().open();
        } catch (DocumentException ex) {
            Logger.getLogger(PDFRender.class.getName())
                  .log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFRender.class.getName())
                  .log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private void renderAnswer() {
        getLayout().setMode(A4Layout.Mode.ANSWER);

        for (int i = 0; i < getPrintList().size(); i++) {
            getLayout().preRender(i, this);
            drawSudoku(getLayout().getDx(), getLayout().getDy(),
                getLayout().getScale(), i, true);
            getLayout().postRender(i, this);
        }
    }

    private void renderSudoku() {
        getLayout().setMode(A4Layout.Mode.SUDOKU);

        for (int i = 0; i < getPrintList().size(); i++) {
            getLayout().preRender(i, this);
            drawSudoku(getLayout().getDx(), getLayout().getDy(),
                getLayout().getScale(), i, false);
            getLayout().postRender(i, this);
        }
    }

    private void drawSudoku(double dx, double dy, double scale, int index,
        boolean isAnswer) {
        getGraphic().translate(dx, dy);
        getGraphic().scale(scale, scale);
        getGraphic().translate(0, 40);

        Grid grid = isAnswer ? getPrintList().get(index).getAnswer()
                             : getPrintList().get(index).getSudoku();
        drawGrid(getGraphic(), index, isAnswer);
        getGraphic().setFont(new Font("Arial", Font.PLAIN, 36));

        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                final int cellValue = grid.getCellValue(i - 1, j - 1);

                if (cellValue > 0) {
                    drawClue(getGraphic(), i, j, cellValue);
                }
            }
        }

        getGraphic().translate(0, -40);
        getGraphic().scale(1 / scale, 1 / scale);
        getGraphic().translate(-dx, -dy);
    }

    /**
     * @return the printList
     */
    public List<PrintRecord> getPrintList() {
        return printList;
    }

    /**
     * @return the writer
     */
    public PdfWriter getWriter() {
        return writer;
    }

    /**
     * @return the layout
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * @return the pdfFile
     */
    public File getPdfFile() {
        return pdfFile;
    }
}
