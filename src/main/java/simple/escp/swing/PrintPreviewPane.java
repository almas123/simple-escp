/*
 * Copyright 2014 Jocki Hendry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simple.escp.swing;

import simple.escp.data.DataSource;
import simple.escp.dom.PageFormat;
import simple.escp.fill.FillJob;
import simple.escp.SimpleEscp;
import simple.escp.Template;
import simple.escp.data.DataSources;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Vector;

/**
 *  Use <code>PrintPreviewPane</code> to display string generated by simple-escp's <code>Template</code>.
 *  The panel includes a print button and printer selection (by name).
 *
 *  <p>Because some configurations for text mode printing is stored in printer's RAM and can't be read using
 *  ESC/P commands, <code>PrintPreviewPane</code> will not attempt to create an exact print preview.
 *  All characters is printed using the same monospace font and ESC/P commands are ignored.  You must also
 *  specificy the page length and page width for print preview.  These values is used for previewing only and
 *  will not affect printing.
 *
 */
public class PrintPreviewPane extends JPanel implements ActionListener {

    public static final int DEFAULT_PAGE_LENGTH = 50;
    public static final int DEFAULT_PAGE_WIDTH = 80;

    private String text;
    private JScrollPane scrollPane;
    private OutputPane outputPane;
    private JToolBar toolbar;
    private JButton printButton;
    private JComboBox<String> printerNameComboBox;

    /**
     * Create a new instance of this class.
     */
    public PrintPreviewPane() {
        outputPane = new OutputPane();
        MouseHandler mouseHandler = new MouseHandler();
        outputPane.addMouseListener(mouseHandler);
        outputPane.addMouseMotionListener(mouseHandler);
        scrollPane = new JScrollPane(outputPane);
        toolbar = new JToolBar();
        printButton = new JButton("Print");
        printButton.addActionListener(this);
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        Vector<String> printerName = new Vector<>();
        printerName.add(PrintServiceLookup.lookupDefaultPrintService().getName());
        for (PrintService printService: printServices) {
            if (!printerName.contains(printService.getName())) {
                printerName.add(printService.getName());
            }
        }
        printerNameComboBox = new JComboBox<>(printerName);
        toolbar.add(printerNameComboBox);
        toolbar.add(printButton);
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Create a new instance of <code>PrintPreviewPane</code> based on a <code>Template</code> and its
     * value (a <code>Map</code> and/or an object).  Template <strong>must</strong> have a valid value
     * for <code>pageLength</code> and <code>pageWidth</code> in its <code>pageFormat</code>.
     *
     * @param template an instance of <code>Template</code>.
     * @param mapValue to fill placholders in the <code>Template</code>.
     * @param objectValue to fill placeholders in the <code>Template</code>.
     */
    public PrintPreviewPane(Template template, Map mapValue, Object objectValue) {
        this(new FillJob(template.parse(), DataSources.from(mapValue, objectValue)).fill(),
            template.getPageFormat().getPageLength(),
            template.getPageFormat().getPageWidth());
    }

    /**
     * Create a new instance of <code>PrintPreviewPane</code>.  You must specify page size for previewing.
     * This page size <strong>doesn't</strong> affect print result.  Page size parameters only affect
     * preview panel.  If you use the different paper size in the printer, the result will be different.
     *
     * @param text the string that will be displayed.
     * @param pageLength page length in number of lines.
     * @param pageWidth page width in number of characters.
     */
    public PrintPreviewPane(String text, int pageLength, int pageWidth) {
        this();
        display(text, pageLength, pageWidth);
    }

    /**
     * Set the data that will be displayed by this <code>PrintPreviewPane</code> and display it.
     *
     * @param text the string that will be displayed.
     * @param pageLength page length in number of lines.
     * @param pageWidth page width in number of characters.
     */
    public void display(String text, int pageLength, int pageWidth) {
        this.text = text;
        outputPane.display(text, pageLength, pageWidth);
        scrollPane.revalidate();
    }

    /**
     * Set the data that will be displayed by this <code>PrintPreviewPane</code> and display it.
     *
     * @param text the string that will be displayed.
     * @param pageFormat the page format that contains information about this report.
     */
    private void display(String text, PageFormat pageFormat) {
        this.text = text;
        int pageLength = DEFAULT_PAGE_LENGTH;
        if (pageFormat.getPageLength() != null) {
            pageLength = pageFormat.getPageLength();
        }
        int pageWidth = DEFAULT_PAGE_WIDTH;
        if (pageFormat.getPageWidth() != null) {
            pageWidth = pageFormat.getPageWidth();
        }
        outputPane.display(text, pageLength, pageWidth);
        scrollPane.revalidate();
    }

    /**
     * Set the data that will be displayed by this <code>PrintPreviewPane</code> and display it.
     *
     * @param template an instance of <code>Template</code>.
     * @param dataSource the data source to fill this template.
     */
    public void display(Template template, DataSource dataSource) {
        PageFormat pageFormat = template.getPageFormat();
        display(new FillJob(template.parse(), dataSource).fill(), pageFormat);
    }

    /**
     * Set the data that will be displayed by this <code>PrintPreviewPane</code> and display it.
     *
     * @param template an instance of <code>Template</code>.
     * @param dataSources the data source to fill this template.
     */
    public void display(Template template, DataSource[] dataSources) {
        PageFormat pageFormat = template.getPageFormat();
        display(new FillJob(template.parse(), dataSources).fill(), pageFormat);
    }

    /**
     * Handler for print button.
     *
     * @param e <code>ActionEvent</code>.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (printerNameComboBox.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "You must select a printer.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new SimpleEscp((String) printerNameComboBox.getSelectedItem()).print(text);
    }

    /**
     * Handler for mouse related events in <code>scrollPane</code>.
     */
    private class MouseHandler extends MouseAdapter {

        private int x, y;

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            JViewport viewport = scrollPane.getViewport();
            Point point = viewport.getViewPosition();
            int newX = point.x - (e.getX() - x);
            int maxX = outputPane.getWidth() - viewport.getWidth();
            int newY = point.y - (e.getY() - y);
            int maxY = outputPane.getHeight() - viewport.getHeight();

            if (newX < 0) {
                newX = 0;
            }
            if (newX > maxX) {
                newX = maxX;
            }
            if (newY < 0) {
                newY = 0;
            }
            if (newY > maxY) {
                newY = maxY;
            }

            viewport.setViewPosition(new Point(newX, newY));
        }

    }
}
