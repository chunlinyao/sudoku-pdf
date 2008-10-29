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

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;


/**
 *
 * @author yaochunlin
 */
public class A4Layout extends Layout {
    private A4Mode mode;
    private Integer renderIndex;

    public A4Layout() {
    }

    private void checkState() throws IllegalStateException {
        if (renderIndex == null) {
            throw new IllegalStateException("Have not call preRender");
        }
    }

    @Override
    double getDx() {
        checkState();

        return mode.getDx()[renderIndex % mode.getDx().length];
    }

    @Override
    double getDy() {
        checkState();

        return mode.getDy()[(renderIndex / mode.getDx().length) % mode.getDy().length];
    }

    @Override
    Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    double getScale() {
        return this.mode.getScale();
    }

    @Override
    void postRender(int i, PDFRender render) {
        this.renderIndex = null;
    }

    @Override
    void preRender(int i, PDFRender render) {
        this.renderIndex = i;

        if ((i % mode.getGridPerPage()) == 0) {
            render.newPage();
        }
    }

    @Override
    void setMode(Mode mode) {
        this.mode = A4Mode.valueOf(mode);
    }

    static class A4Mode {
        public static final A4Mode SUDOKU;
        public static final A4Mode ANSWER;

        static {
            SUDOKU = new A4Mode(new double[] { 10, 280 },
                    new double[] { 80, 421 }, 0.5);
            ANSWER = new A4Mode(new double[] { 20, 155, 290, 425 },
                    new double[] { 40, 195, 350, 505, 660 }, 0.25);
        }

        private final double[] dx;
        private final double[] dy;
        private final int gridPerPage;
        private final double scale;

        private A4Mode(double[] dx, double[] dy, double scale) {
            this.dx = dx;
            this.dy = dy;
            this.gridPerPage = dx.length * dy.length;
            this.scale = scale;
        }

        /**
         * @return the dx
         */
        public double[] getDx() {
            return dx;
        }

        /**
         * @return the dy
         */
        public double[] getDy() {
            return dy;
        }

        /**
         * @return the gridPerPage
         */
        public int getGridPerPage() {
            return gridPerPage;
        }

        /**
         * @return the scale
         */
        public double getScale() {
            return scale;
        }

        static A4Mode valueOf(Layout.Mode mode) {
            switch (mode) {
            case SUDOKU:
                return SUDOKU;

            case ANSWER:
                return ANSWER;

            default:
                return SUDOKU;
            }
        }
    }
}
